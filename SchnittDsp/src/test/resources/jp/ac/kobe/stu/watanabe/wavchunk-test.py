import lib.wavchunk as wavchunk
import lib.dsp_py as dsp
import wave
import sys
import numpy as np
from pylab import * 

argv = sys.argv

filename     = argv[1]
FFTN         = 512
WINDOW       = "Hanning"
PREEMPH_COEF = 0.97
CH           = 20
HZ           = 16000
PATH         = "/home/snoopy/workspace/SchnittDsp/src/test/testResource/mfcc"

#########################
# Set parmeters
#########################
dsp = dsp.Dsp()
dsp.set_filename(filename)             \
   .set_preemph_coef(PREEMPH_COEF)     \
   .set_windowtype(WINDOW)             \
   .set_fftn(FFTN)                     \
   .set_mfcc_ch(CH)

samples = dsp.do_wav2int()
samples = samples[:FFTN]

wavchunk = wavchunk.WavChunk(samples, HZ)
wavchunk.do_preemp(PREEMPH_COEF)
wavchunk.do_hanning()
mfc = wavchunk.do_mfcc(FFTN, CH)
mfcc = wavchunk.do_dct()

print "mfcc", mfcc[:12]
plot(range(0, 12), mfcc[:12])
savefig("mfcc_wavchunk.png")
show()

wavchunk.write_dct(PATH)
