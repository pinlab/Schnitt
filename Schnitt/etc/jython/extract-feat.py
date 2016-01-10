import schnitt
from schnitt import logger
import sys, os


# extract features using .jar 
# input: .wav       sound
#        .textgrid  voices marked


# wav -> DoubleFrameTier -> DoubleFrameTier   
#                         + BinaryTargetTier :  
# FrameAlignment  
# takes : DoubleFrameTier + BinaryTargetTier
# calc overlaps
#    ----|+++++|---------
#     |     |     |     |
# 
# -> if at least 5%(?or 10%?) overlap : it is an ON frame
#   -> save ON/OFF frames + features into file:
#<sil>, f1, f2, f3... fn
#<s>,   f1, f2, f3... fn

wav = None

for arg in sys.argv:
    if arg[-4:] == ".wav" and os.path.isfile(arg): 
        wav = os.path.abspath(arg)
        break

if not wav:
    logger.error("No .wav file to process!")
    sys.exit(-1)
 
logger.info("Wav is '" + wav + "'")

from info.pinlab.pinsound import WavClip
from info.pinlab.snd.fe.ParamSheet import ParamSheetBuilder
from info.pinlab.snd.fe import FEParam
from info.pinlab.snd.fe import StreamingAcousticFrontEnd
from info.pinlab.snd.trs import DoubleFrameTier


frame_processors  = ""
frame_processors += "info.pinlab.snd.fe.HanningWindower"
frame_processors += ":info.pinlab.snd.fe.Fft"
frame_processors += ":info.pinlab.snd.fe.PowerCalculator"
params = ParamSheetBuilder().set(FEParam.FRAME_PROCESSORS, frame_processors).set(FEParam.HZ, 16000).set(FEParam.FRAME_LEN_MS, 50).build()

fe = StreamingAcousticFrontEnd(params);
fe.setWav(WavClip(wav))
fe.start()

tier = fe.getFrameTier();

for t in tier.getTimeLabels():
    frame = tier.getFrameAt(t)
    arr = frame.getArray("power")
    print ", ".join(map(str, arr))

