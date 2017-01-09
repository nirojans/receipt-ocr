#!/bin/bash
mean="$(convert $1 -colorspace gray -format "%[fx:100*mean]" info:)"
mean=${mean%.*}
#threshold=$(echo "100 - $mean" | bc)
threshold=40
if [ "$mean" -ge 45 ] ; then
threshold=20
fi
echo $threshold
sh magicwand 10,10 -t $threshold -f image -r outside $1 magic.tif
convert  -normalize -level 10%,90% -sharpen 0x1 magic.tif sharpen.tif
sh textcleaner  -g -e normalize -f 15 -o 10 sharpen.tif clean.tif
convert clean.tif -resize 300% clean_big.tif
tesseract clean_big.tif sharp_clean_resize_result -c preserve_interword_spaces=1 -psm 4

