#!/bin/bash
# $1 = rawfile, $2 = file name, $3 = preprocessfolder/, $4 = outputfolder/, $5 = archivefolder/
sh scripts/localthresh -m 3 -r 25 -b 20 -n yes $1 $3$2"_2_temp_thresh.tiff"
convert  -normalize -level 10%,90% -sharpen 0x1 $3$2"_2_temp_thresh.tiff" $3$2"_2.tiff"
tesseract $3$2"_2.tiff" $4$2"_2_temp" -c preserve_interword_spaces=1 -psm 4
rm $3$2"_2_temp_thresh.tiff"
awk 'NF' $4$2"_2_temp.txt" > $4$2"_2"
rm $4$2"_2_temp.txt"