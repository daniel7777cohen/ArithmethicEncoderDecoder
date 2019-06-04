//DanielCohen 203850029
//BenEfrat 305773806

import java.io.IOException;

public interface Compressor
{
	abstract public void Compress(String[] input_names, String[] output_names) throws IOException;
	abstract public void Decompress(String[] input_names, String[] output_names) throws IOException;

	abstract public byte[] CompressWithArray(String[] input_names, String[] output_names) throws IOException;
	abstract public byte[] DecompressWithArray(String[] input_names, String[] output_names,byte [] endodedbyte) throws IOException;
}
