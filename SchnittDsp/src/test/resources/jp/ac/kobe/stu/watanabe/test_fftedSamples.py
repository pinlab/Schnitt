#! coding: utf-8

import lib.dsp_py as dsp_py
import sys
from pylab import *

##########################
# Input: wav file name for processing
#########################

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
win_samp_no = int((WINDOW_LEN / 1000.0) * SMP_RATE)
assert(win_samp_no == 320)

# get integer
int_samples = dsp.do_wav2int()
# get preemphed integer
tempSamples = [0] * (FFTN)
tempSamples[:win_samp_len] = int_samples[:win_samp _len]
preemphed_samples = dsp.do_preemph(tempSamples)
windowed_samples = dsp.do_windowning(preemphed_samples)

### applying FFT
fft_spec = dsp.do_fft(windowed_samples, plotting=False, saving=False)

### fft samples
fft_samples_file = "fft_samples.txt"
f = open(fft_samples_file, "w")
for i in range(0, FFTN/2):
    value = str(fft_spec[i]) + "\n"
    f.write(value)

f.close()
