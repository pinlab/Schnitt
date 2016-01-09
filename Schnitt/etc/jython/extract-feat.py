
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



