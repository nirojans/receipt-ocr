#!/bin/bash
# $1 = rawfile, $2 = file name, $3 = preprocessfolder/, $4 = outputfolder/, $5 = archivefolder/
sh scripts/textcleaner  -g -e normalize -f 15 -o 10 $1 $3$2"_1_temp_clean.tiff"
convert $3$2"_1_temp_clean.tiff" -density 300 $3$2"_1.tiff"
tesseract $3$2"_1.tiff" $4$2"_1_temp" -c preserve_interword_spaces=1 -psm 4
rm $3$2"_1_temp_clean.tiff"
#mv $1 $5
awk 'NF' $4$2"_1_temp.txt" > $4$2"_1"
rm $4$2"_1_temp.txt"
