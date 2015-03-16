ext\png2yuv.exe -I p -f 24 -j tmp\img%%05d.png -n %1 -b 1 > tmp\temp.yuv
ext\vpxenc.exe --good --cpu-used=0 --auto-alt-ref=1 --lag-in-frames=16 --end-usage=vbr --passes=2 --threads=2 --target-bitrate=3000 -o %2 tmp\temp.yuv
del tmp\*.png
del tmp\temp.yuv