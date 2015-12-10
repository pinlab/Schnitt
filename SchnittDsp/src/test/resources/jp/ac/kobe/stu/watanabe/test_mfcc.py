#! coding: utf-8

import lib.dsp_py as dsp_py
import lib.wavchunk as wavchunk
import sys
from pylab import *

##########################
# Input: wav file name for processing
#########################

mfcc_file = "test_mfcc.txt"

# Parameter
argv          = sys.argv
wav_filename  = argv[1]
FFTN          = 512
WINDOW_LEN    = 20 #ms
WINDOW        = "Hanning"
PREEMPH_COEF  = 0.97
CH            = 20
SMP_RATE      = 16000
SHIFT_LEN     = 10 #ms#


# Set parmeters
dsp = dsp_py.Dsp()
dsp.set_filename(wav_filename)         \
   .set_preemph_coef(PREEMPH_COEF)     \
   .set_windowtype(WINDOW)             \
   .set_fftn(FFTN)                     \
   .set_mfcc_ch(CH)


# sample number of window
win_samp_len = int((WINDOW_LEN / 1000.0) * SMP_RATE)
assert(win_samp_len == 320)

# get integer
int_samples = dsp.do_wav2int()
# get preemphed integer
tempSamples = [0] * (FFTN)
tempSamples[:win_samp_len] = int_samples[:win_samp_len]

##
preemphed_samples = dsp.do_preemph(tempSamples)
windowed_samples = dsp.do_windowning(preemphed_samples)
fft_spec = dsp.do_fft(windowed_samples, plotting=False, saving=False)
pow_spec = [i **2 for i in fft_spec]

mfc = dsp.do_melFilterBank(pow_spec)
mfccArr = dsp.do_mfcc(mfc)

#chunk = wavchunk.WavChunk(tempSamples, SMP_RATE)
#chunk.do_preemp(0.97)
#chunk.so_hanning()
#mfc = chunk.do_mfcc(FFTN, CH)
#mfccArr = do_dct()

### fft samples

f = open(mfcc_file, "w")
for i in mfccArr:
    value = str(i) + "\n"
    print value
    f.write(value)

f.close()
