#!/bin/sh
#!/usr/bin/python2.7

# 编译环境安装在tensor的虚拟环境中
echo "需要先启动tensor虚拟环境"
echo "提交到主分支"
time=`date "+%Y-%m-%d-%H"`
echo "提交时间${time}"
git add . --all
git commit -am ${time}
git push origin develop
