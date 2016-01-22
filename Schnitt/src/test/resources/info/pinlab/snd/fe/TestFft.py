#! coding: utf-8

#########
# FftTest.py
# Create test files for FftTest
#########

import Fe
import numpy as np

# parameter
hz = 16000
fftn = 512
fft_signal_len = 320

output_file = __file__[:-3] + ".txt"
test_arr = []

if __name__ == "__main__":
    test_arr = np.hanning(fft_signal_len)
    fe = Fe.Fe()                    \
        .set_hz(hz)                 \
        .set_fftn(fftn)

    # Apply Fft 
    fft_samp = fe.do_fft(test_arr)
    # Write the results 
    f = open(output_file, "w")
    for i in range(0, len(fft_samp)):
        f.write(str(fft_samp[i]) + "\n")
    f.close()

    print "finish: ", __file__, "\n", \
    "fft_samp_num: ",fft_signal_len,  \
    "\n", "fftn: ", fftn, "\n",       \
    "output file: ", output_file
