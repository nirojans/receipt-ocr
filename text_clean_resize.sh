#!/bin/bash
sh textcleaner  -g -e normalize -f 15 -o 10 $1 clean.tif
convert clean.tif -resize 300% clean_big.tif
tesseract clean_big.tif sharp_clean_resize_result -c preserve_interword_spaces=1 -psm 4

