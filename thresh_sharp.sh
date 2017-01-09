#!/bin/bash
sh localthresh -m 3 -r 25 -b 20 -n yes $1 threshold.tif
convert  -normalize -level 10%,90% -sharpen 0x1 threshold.tif sharpen.tif
#sh textcleaner  -g -e normalize -f 15 -o 10 sharpen.tif clean.tif
#convert threshold.tif -resize 300% clean_big.tif
tesseract sharpen.tif sharp_clean_resize_result -c preserve_interword_spaces=1 -psm 4

