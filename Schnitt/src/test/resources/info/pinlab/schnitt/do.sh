# create headerless files (double)
sox sample.wav long-sample.dat
# remove first 2 lines manually
# extract samples
cat long-sample.dat  | tr -s ' ' | cut -d ' ' -f2 > long-sample[time].dat

# in R: plot

