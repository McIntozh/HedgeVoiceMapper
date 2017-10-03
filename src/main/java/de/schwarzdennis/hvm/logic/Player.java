package de.schwarzdennis.hvm.logic;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * TODO: very ugly...
 */
public class Player implements Runnable {

    private static final int BUFSIZE = 4096 * 2;
    private int RETRY = 3;
    private SyncState oy;
    private StreamState os;
    private Page og;
    private Packet op;
    private Info vi;
    private Comment vc;
    private DspState vd;
    private Block vb;
    private int rate = 0;
    private int channels = 0;
    private byte[] buffer = null;
    private int bytes = 0;
    private SourceDataLine outputLine = null;
    private String fileName;
    private Thread playerThread;

    public void play(String fileName) {
        if (playerThread != null) {
            stop();
        }
        this.fileName = fileName;
        playerThread = new Thread(this);
        playerThread.start();
    }

    public void stop() {
        playerThread.stop();
        playerThread = null;
    }

    public void run() {
        try {

            play_stream();

        } catch (Exception ex) {
            Logger.getLogger(Player.class.getName()).log(Level.WARNING, null, ex);
        }

    }

    private void play_stream() {
        if (fileName == null || !new File(fileName).exists()) {
            return;
        }

        InputStream bitStream = null;
        try {
            bitStream = new FileInputStream(fileName);

            boolean chained = false;

            init_jorbis();

            loop:
            while (true) {
                int eos = 0;

                int index = oy.buffer(BUFSIZE);
                buffer = oy.data;

                bytes = bitStream.read(buffer, index, BUFSIZE);

                oy.wrote(bytes);

                if (chained) { //
                    chained = false; //   
                } //
                else { //
                    if (oy.pageout(og) != 1) {
                        if (bytes < BUFSIZE) {
                            break;
                        }
                        System.err.println("Input does not appear to be an Ogg bitstream.");
                        return;
                    }
                } //
                os.init(og.serialno());
                os.reset();

                vi.init();
                vc.init();

                if (os.pagein(og) < 0) {
                    // error; stream version mismatch perhaps
                    System.err.println("Error reading first page of Ogg bitstream data.");
                    return;
                }

                if (os.packetout(op) != 1) {
                    // no page? must not be vorbis
                    System.err.println("Error reading initial header packet.");
                    break;
                    //      return;
                }

                if (vi.synthesis_headerin(vc, op) < 0) {
                    // error case; not a vorbis header
                    System.err.println("This Ogg bitstream does not contain Vorbis audio data.");
                    return;
                }

                int i = 0;

                while (i < 2) {
                    while (i < 2) {
                        int result = oy.pageout(og);
                        if (result == 0) {
                            break; // Need more data
                        }
                        if (result == 1) {
                            os.pagein(og);
                            while (i < 2) {
                                result = os.packetout(op);
                                if (result == 0) {
                                    break;
                                }
                                if (result == -1) {
                                    break loop;
                                }
                                vi.synthesis_headerin(vc, op);
                                i++;
                            }
                        }
                    }

                    index = oy.buffer(BUFSIZE);
                    buffer = oy.data;

                    bytes = bitStream.read(buffer, index, BUFSIZE);

                    if (bytes == 0 && i < 2) {
                        return;
                    }
                    oy.wrote(bytes);
                }

                int convsize = BUFSIZE / vi.channels;

                vd.synthesis_init(vi);
                vb.init(vd);

                float[][][] _pcmf = new float[1][][];
                int[] _index = new int[vi.channels];

                getOutputLine(vi.channels, vi.rate);

                byte[] convbuffer = new byte[convsize];

                while (eos == 0) {
                    while (eos == 0) {

                        int result = oy.pageout(og);
                        if (result == 0) {
                            break; // need more data
                        }
                        if (result == -1) { // missing or corrupt data at this page position
                            //	    System.err.println("Corrupt or missing data in bitstream; continuing...");
                        } else {
                            os.pagein(og);

                            if (og.granulepos() == 0) { //
                                chained = true; //
                                eos = 1; // 
                                break; //
                            } //

                            while (true) {
                                result = os.packetout(op);
                                if (result == 0) {
                                    break; // need more data
                                }
                                if (result == -1) { // missing or corrupt data at this page position
                                    // no reason to complain; already complained above
                                    //System.err.println("no reason to complain; already complained above");
                                } else {
                                    // we have a packet.  Decode it
                                    int samples;
                                    if (vb.synthesis(op) == 0) { // test for success!
                                        vd.synthesis_blockin(vb);
                                    }
                                    while ((samples = vd.synthesis_pcmout(_pcmf, _index)) > 0) {
                                        float[][] pcmf = _pcmf[0];
                                        int bout = (samples < convsize ? samples : convsize);

                                        // convert doubles to 16 bit signed ints (host order) and
                                        // interleave
                                        for (i = 0; i < vi.channels; i++) {
                                            int ptr = i * 2;
                                            //int ptr=i;
                                            int mono = _index[i];
                                            for (int j = 0; j < bout; j++) {
                                                int val = (int) (pcmf[i][mono + j] * 32767.);
                                                if (val > 32767) {
                                                    val = 32767;
                                                }
                                                if (val < -32768) {
                                                    val = -32768;
                                                }
                                                if (val < 0) {
                                                    val = val | 0x8000;
                                                }
                                                convbuffer[ptr] = (byte) (val);
                                                convbuffer[ptr + 1] = (byte) (val >>> 8);
                                                ptr += 2 * (vi.channels);
                                            }
                                        }
                                        outputLine.write(convbuffer, 0, 2 * vi.channels * bout);
                                        vd.synthesis_read(bout);
                                    }
                                }
                            }
                            if (og.eos() != 0) {
                                eos = 1;
                            }
                        }
                    }

                    if (eos == 0) {
                        index = oy.buffer(BUFSIZE);
                        buffer = oy.data;
                        try {
                            bytes = bitStream.read(buffer, index, BUFSIZE);
                        } catch (Exception e) {
                            System.err.println(e);
                            return;
                        }
                        if (bytes == -1) {
                            break;
                        }
                        oy.wrote(bytes);
                        if (bytes == 0) {
                            eos = 1;
                        }
                    }
                }

                os.clear();
                vb.clear();
                vd.clear();
                vi.clear();
            }

            oy.clear();
        } catch (Exception e) {
            Logger.getLogger(Player.class.getName()).log(Level.WARNING, null, e);
        } finally {
            try {
                if (bitStream != null) {
                    bitStream.close();
                }
            } catch (Exception e) {
            }
        }
    }

    void init_jorbis() {
        oy = new SyncState();
        os = new StreamState();
        og = new Page();
        op = new Packet();

        vi = new Info();
        vc = new Comment();
        vd = new DspState();
        vb = new Block(vd);

        buffer = null;
        bytes = 0;

        oy.init();
    }

    SourceDataLine getOutputLine(int channels, int rate) {
        if (outputLine == null || this.rate != rate || this.channels != channels) {
            if (outputLine != null) {
                outputLine.drain();
                outputLine.stop();
                outputLine.close();
            }
            init_audio(channels, rate);
            outputLine.start();
        }
        return outputLine;
    }

    void init_audio(int channels, int rate) {
        try {

            AudioFormat audioFormat = new AudioFormat((float) rate, 16, channels, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
            if (!AudioSystem.isLineSupported(info)) {
                return;
            }

            try {
                outputLine = (SourceDataLine) AudioSystem.getLine(info);
                outputLine.open(audioFormat);
            } catch (Exception ex) {
                Logger.getLogger(Player.class.getName()).log(Level.WARNING, null, ex);
                return;
            }

            this.rate = rate;
            this.channels = channels;
        } catch (Exception ee) {
            Logger.getLogger(Player.class.getName()).log(Level.WARNING, null, ee);
        }
    }
}
