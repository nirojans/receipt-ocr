#!/bin/bash
mean="$(convert $1 -colorspace gray -format "%[fx:100*mean]" info:)"
mean=${mean%.*}
#threshold=$(echo "100 - $mean" | bc)
threshold=40
if [ "$mean" -ge 45 ] ; then
threshold=20
fi
echo $threshold
sh scripts/magicwand 10,10 -t $threshold -f image -r outside $1 $3$2"_3_temp_magic.tiff"
convert  -normalize -level 10%,90% -sharpen 0x1 $3$2"_3_temp_magic.tiff" $3$2"_3_temp_sharp.tiff"
sh scripts/textcleaner  -g -e normalize -f 15 -o 10 $3$2"_3_temp_sharp.tiff" $3$2"_3_temp_clean.tiff"
convert $3$2"_3_temp_clean.tiff" -resize 300% $3$2"_3.tiff"
tesseract $3$2"_3.tiff" $4$2"_3_temp" -c preserve_interword_spaces=1 -psm 4
rm $3$2"_3_temp_magic.tiff"
rm $3$2"_3_temp_sharp.tiff"
rm $3$2"_3_temp_clean.tiff"
awk 'NF' $4$2"_3_temp.txt" > $4$2"_3"
rm $4$2"_3_temp.txt"

