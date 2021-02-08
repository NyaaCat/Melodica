package cat.nyaa.melodica.utils;

import cat.nyaa.melodica.api.IMusicSheet;
import cat.nyaa.melodica.api.ITrack;
import cat.nyaa.melodica.sheet.MusicSheetImpl;
import cat.nyaa.melodica.sheet.NoteImpl;
import cat.nyaa.melodica.sheet.SheetRecord;
import cat.nyaa.melodica.sheet.TrackImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Utils for reading Note Block Studio data
 * modified by ReinWD
 * @Author xxmicloxx @ NoteBlockAPI
 */
public class NBSDecoder {

	public static IMusicSheet parse(File songFile) {
		try {
			return parse(new FileInputStream(songFile), songFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static IMusicSheet parse(InputStream inputStream) {
		return parse(inputStream, null); // Source is unknown -> no file
	}

	/**
	 * Parses a Song from an InputStream and a Note Block Studio project file (.nbs)
	 * @see IMusicSheet
	 * @param inputStream of a .nbs file
	 * @param songFile representing a .nbs file
	 * @return Song object representing the given .nbs file
	 */
	private static IMusicSheet parse(InputStream inputStream, File songFile) {
		HashMap<Integer, ITrack> layerHashMap = new HashMap<Integer, ITrack>();
		byte biggestInstrumentIndex = -1;
		boolean isStereo = false;
		try {
			DataInputStream dataInputStream = new DataInputStream(inputStream);
			short length = readShort(dataInputStream);
			int firstcustominstrument = 10; //Backward compatibility - most of songs with old structure are from 1.12
			int firstcustominstrumentdiff;
			int nbsversion = 0;
			if (length == 0) {
				nbsversion = dataInputStream.readByte();
				firstcustominstrument = dataInputStream.readByte();
				if (nbsversion >= 3) {
					length = readShort(dataInputStream);
				}
			}
			firstcustominstrumentdiff = InstrumentUtils.getCustomInstrumentFirstIndex() - firstcustominstrument;
			short songHeight = readShort(dataInputStream);
			String title = readString(dataInputStream);
			String author = readString(dataInputStream);
			String originalAuthor = readString(dataInputStream); // original author
			String description = readString(dataInputStream);
			float speed = readShort(dataInputStream) / 100f;
			dataInputStream.readBoolean(); // auto-save
			dataInputStream.readByte(); // auto-save duration
			dataInputStream.readByte(); // x/4ths, time signature
			readInt(dataInputStream); // minutes spent on project
			readInt(dataInputStream); // left clicks (why?)
			readInt(dataInputStream); // right clicks (why?)
			readInt(dataInputStream); // blocks added
			readInt(dataInputStream); // blocks removed
			readString(dataInputStream); // .mid/.schematic file name
			if (nbsversion >= 4) {
				dataInputStream.readByte(); // loop on/off
				dataInputStream.readByte(); // max loop count
				readShort(dataInputStream); // loop start tick
			}
			short tick = -1;
			while (true) {
				short jumpTicks = readShort(dataInputStream); // jumps till next tick
				//System.out.println("Jumps to next tick: " + jumpTicks);
				if (jumpTicks == 0) {
					break;
				}
				tick += jumpTicks;
				//System.out.println("Tick: " + tick);
				short layer = -1;
				while (true) {
					short jumpLayers = readShort(dataInputStream); // jumps till next layer
					if (jumpLayers == 0) {
						break;
					}
					layer += jumpLayers;
					//System.out.println("Layer: " + layer);
					byte instrument = dataInputStream.readByte();

					if (firstcustominstrumentdiff > 0 && instrument >= firstcustominstrument){
						instrument += firstcustominstrumentdiff;
					}

					byte key = dataInputStream.readByte();
					byte velocity = 100;
					int panning = 100;
					short pitch = 0;
					if (nbsversion >= 4) {
						velocity = dataInputStream.readByte(); // note block velocity
						panning = 200 - dataInputStream.readUnsignedByte(); // note panning, 0 is right in nbs format
						pitch = readShort(dataInputStream); // note block pitch
					}

					if (panning != 100){
					    isStereo = true;
                    }
					setNote(layer, tick,
							new NoteImpl(InstrumentUtils.getInstrument(instrument),  (velocity / 100_00_00_00F)
							* ((1F / 16F)), getPitchTransposed(key, pitch)),
							layerHashMap);
				}
			}

			if (nbsversion > 0 && nbsversion < 3) {
				length = tick;
			}

			for (int i = 0; i < songHeight; i++) {
				ITrack layer = layerHashMap.get(i);

				String name = readString(dataInputStream);
				if (nbsversion >= 4){
					dataInputStream.readByte(); // layer lock
				}

				byte volume = dataInputStream.readByte();
				int panning = 100;
				if (nbsversion >= 2){
					panning = 200 - dataInputStream.readUnsignedByte(); // layer stereo, 0 is right in nbs format
				}

                if (panning != 100){
                    isStereo = true;
                }

				if (layer != null) {
					layer.setName(name);
					layer.setVolume(volume);
					layer.setPanning(panning);
				}
			}
			//ignore custom instruments

			MusicSheetImpl musicSheet = new MusicSheetImpl();
			layerHashMap.forEach((k, v) -> {
				musicSheet.addTrack(String.valueOf(k), v);
			});
			musicSheet.compile();
			return musicSheet;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (EOFException e) {
			String file = "";
			if (songFile != null) {
				file = songFile.getName();
			}
			Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Song is corrupted: " + file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Sets a note at a tick in a song
	 * @param layerIndex
	 * @param ticks
	 * @param note
	 * @param layerHashMap
	 */
	private static void setNote(int layerIndex, int ticks, NoteImpl note, HashMap<Integer, ITrack> layerHashMap) {
		ITrack layer = layerHashMap.get(layerIndex);
		if (layer == null) {
			layer = new TrackImpl(String.valueOf(layerIndex));
			layerHashMap.put(layerIndex, layer);
		}
		layer.getSheetRecords().add(new SheetRecord(ticks, note));
	}

	private static short readShort(DataInputStream dataInputStream) throws IOException {
		int byte1 = dataInputStream.readUnsignedByte();
		int byte2 = dataInputStream.readUnsignedByte();
		return (short) (byte1 + (byte2 << 8));
	}

	private static int readInt(DataInputStream dataInputStream) throws IOException {
		int byte1 = dataInputStream.readUnsignedByte();
		int byte2 = dataInputStream.readUnsignedByte();
		int byte3 = dataInputStream.readUnsignedByte();
		int byte4 = dataInputStream.readUnsignedByte();
		return (byte1 + (byte2 << 8) + (byte3 << 16) + (byte4 << 24));
	}

	private static String readString(DataInputStream dataInputStream) throws IOException {
		int length = readInt(dataInputStream);
		StringBuilder builder = new StringBuilder(length);
		for (; length > 0; --length) {
			char c = (char) dataInputStream.readByte();
			if (c == (char) 0x0D) {
				c = ' ';
			}
			builder.append(c);
		}
		return builder.toString();
	}

	public static float getPitchTransposed(byte key, short pitch) {
		// Apply key to pitch
		pitch += key * 100;

		while (pitch < 3300) pitch += 1200;
		while (pitch > 5700) pitch -= 1200;

		pitch -= 3300;

		return pitches[pitch];
	}

	private static float[] pitches = null;

	static {
		pitches = new float[2401];

		for (int i = 0; i < 2401; i++){
			pitches[i] = (float) Math.pow(2, (i - 1200d) / 1200d);
		}
	}
}
