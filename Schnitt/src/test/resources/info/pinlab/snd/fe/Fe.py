#! coding: utf-8

import scipy.signal
import sys
import wave
import os
import numpy as np
from pylab import *

class Fe:
    """ Class for digital signal processing
        This HAVE TO set:
            - (1) wav file names
            - (2) preemphasized coefficients
            - (3) window function type
            - (4) fft size
        This can calculate:
            - (1) preemphsized samples
            - (2) windowed sampels
            - (3) amplitude spectrum
            - (4) mfcc
    """
    def __init__(self):
        # parameter
        self.filename     = ""
        self.samples      = []
        self.preemph_coef = 0.97
        self.windowtype   = ""
        self.fftn         = 512
        self.hz           = 16000
        self.mfcc_ch      = 20
        self.window_len   = 20 # ms
        self.wav_path="."
        print os.path.abspath(__file__)
        
    def set_wavfile(self, filename):
        """Set filename & Samples"""
        filename = os.path.join(self.wav_path, filename)
        if os.path.exists(filename):
            self.filename = filename
        else: print ("No file exists")
        return self
    def set_fftn(self, fftn):
        """Set fftN"""
        if fftn > 0 and (fftn & (fftn - 1)) == 0:
            self.fftn = fftn
        else: print "[ERROR]: fftn has to be the power of 2"
        return self
    def set_hz(self, hz):
        """set sampling rate"""
        self.hz = hz
        return self
    def set_preemph_coef(self, preemph_coef):
        """Set Preemph coef"""
        self.preemph_coef = preemph_coef
        return self
    def set_windowtype(self, windowtype="Hanning"):
        """Set windowtype: Hanning or Hamming"""
        if windowtype == "Hanning" or windowtype == "Hamming":
            self.windowtype = windowtype
        else: 
            print("[ERROR] window type = Hamming or Hanning")
        return self
    def set_windowlen(self, window_len):
        """ Set window length"""
        self.window_len = window_len #ms#
        self.window_samp_len = window_len*self.hz*0.001
        return self
    def set_mfcc_ch(self, ch):
        """Set MFCC ch"""
        if isinstance(ch, int): 
            self.mfcc_ch = ch
        else:
            raise Exception("Mfcc ch has to be integer")
        return self

    ### Method fotr DSP
    def do_wav2int(self):
        """read wav sampls"""
        try:
            self.wf = wave.open(self.filename, "r")
            self.hz = self.wf.getframerate()
            self.samples = self.wf.readframes(self.wf.getnframes())
            self.samples = np.frombuffer(self.samples, dtype="int16")
        except IOError:
            print "Cannot read the file!"
    def get_intsamples(self):
        """return integer array of wav file"""
        return self.samples
    def do_preemph(self, signal):
        """ apply preemphasise"""
        return scipy.signal.lfilter([1.0, -self.preemph_coef], 1, signal)
    def do_windowning(self, signal):
        """apply windowning to input signal"""
        if  self.windowtype  == "Hamming":
            self.window = np.hamming(self.window_samp_len)
        elif self.windowtype == "Hanning":
            self.window = np.hanning(self.window_samp_len)
        return signal * self.window
    def do_fft(self, samples):
        """apply fft to input samples
           input = list
           [1] length of samples > fftn: use the first elements (n=fftn sizes) of samples
           [2] length 0f samples < fftn: 0-padding
           output: maginitude"""
        self.nyq = self.fftn/2
        if len(samples) == self.fftn:
            self.signal = samples
        # 0-padding
        elif len(samples) < self.fftn:
            self.zero_list = [0] * self.fftn
            self.zero_list[0:len(samples)] = samples
            self.signal = self.zero_list
            print "0-padding"
        # chopping 
        elif len(samples) > self.fftn:
            self.signal =  samples[:self.fftn]
            print "chopping"

        self.N = len(self.signal)
        #print self.signal
        self.normfactor = 1.4142135623730951 / self.N
        self.fftdata = np.fft.fft(self.signal)
        self.fftdata = self.fftdata[:self.N / 2]
        self.mag = self.normfactor * np.abs(self.fftdata)
        return self.mag

    def hz2mel(self, f):
        return 1127.01048 * np.log(f / 700.0 + 1.0)

    def mel2hz(self, m):
        return 700.0 * (np.exp(m / 1127.01048) - 1.0)
    def do_melFilterBank(self, ampArr):
        self.signal = ampArr
        self.fmax = self.hz / 2.0
        self.melmax = self.hz2mel(self.fmax)
        self.nmax = self.fftn/ 2.0
        self.df = self.hz / float(self.fftn)
        self.dmel = self.melmax / (self.mfcc_ch + 1.0)
        self.melcenters = np.arange(1.0, self.mfcc_ch + 1.0) * self.dmel
        
        self.fcenters = 700.0 * (np.exp(self.melcenters / 1127.01048) - 1.0)
        self.indexcenter = np.round(self.fcenters / self.df)

        # 各フィルタの開始位置のインデックス
        self.indexstart = np.hstack(([0], self.indexcenter[0:self.mfcc_ch - 1]))
        # 各フィルタの終了位置のインデックス
        self.indexstop = np.hstack((self.indexcenter[1:self.mfcc_ch], [self.nmax]))

        self.filterbank = np.zeros((self.mfcc_ch, self.nmax))
        for c in np.arange(0, self.mfcc_ch):
            # 三角フィルタの左の直線の傾きから点を求める
            self.increment= 1.0 / (self.indexcenter[c] - self.indexstart[c])
            for i in np.arange(self.indexstart[c], self.indexcenter[c]):
                self.filterbank[c, i] = (i - self.indexstart[c]) * self.increment
            # 三角フィルタの右の直線の傾きから点を求める
            self.decrement = 1.0 / (self.indexstop[c] - self.indexcenter[c])
            for i in np.arange(self.indexcenter[c], self.indexstop[c]):
                self.filterbank[c, i] = 1.0 - ((i - self.indexcenter[c]) * self.decrement)

        self.mfc = np.dot(self.signal, self.filterbank.T)
        return self.mfc


    def do_mfcc(self, mspec):
        self.log_mfc = np.log10(mspec)
        #self.dct = scipy.fftpack.dct(self.log_mfc, type=2, norm="ortho", axis=-1)
        self.dct = scipy.fftpack.dct(self.log_mfc, type=2, norm="ortho", axis=-1)        
        return self.dct[:12]
