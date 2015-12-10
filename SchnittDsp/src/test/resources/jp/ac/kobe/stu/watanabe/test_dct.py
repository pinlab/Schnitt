#! coding:utf_8

######
# This is the program for testing discrete cosine translation
#####

import scipy.signal
import numpy as np

output_filename = "test_dct.txt"
test_samples = np.array([1.0000, 1.0000, 1.0000, 1.0000, 2.0000, 2.0000, 2.0000, 2.0000, 2.0000])

print "[test samples] ", test_samples
dct_arr = scipy.fftpack.dct(test_samples, type=2, norm="ortho", axis=-1)
print "[result] ", dct_arr


f = open(output_filename, "w")
for i in dct_arr:
    f.write(str(i) + "\n")

f.close()





