# -*- coding: UTF-8 -*-
import os

import cv2
import numpy as np


def ruihua(fp, op):
    image = cv2.imread(fp)
    kernel = np.array([[0, -1, 0], [-1, 5, -1], [0, -1, 0]], np.float32)
    dst = cv2.filter2D(image, -1, kernel=kernel)
    cv2.imwrite(op, dst, [cv2.IMWRITE_PNG_COMPRESSION, 0])


def piplechain(fp, op):
    img = cv2.imread(fp, flags=cv2.IMREAD_GRAYSCALE)
    ret,timg=cv2.threshold(img,200,255,cv2.THRESH_BINARY)
    # 创建 核
    # kernel = np.ones((2, 2), np.uint8)
    # 腐蚀
    # erorsion_img = cv2.erode(timg, kernel, iterations=1)

    #膨胀
    # erorsion_img = cv2.dilate(erorsion_img, kernel, iterations=1)


    kernel = np.array([[0, -1, 0], [-1, 6, -1], [0, -1, 0]], np.float32)
    dst = cv2.filter2D(timg, -1, kernel=kernel)
    cv2.imwrite(op, dst, [cv2.IMWRITE_PNG_COMPRESSION, 0])


if __name__ == "__main__":
    hk = "/media/lame/0DD80F300DD80F30/code/ok-pdf/src/main/resources/hk"
    for root, dirs, files in os.walk("/media/lame/0DD80F300DD80F30/code/ok-pdf/src/main/resources/hashhell",
                                     topdown=False):
        for name in files:
            print("\n正在处理图片" + os.path.join(root, name))
            piplechain(os.path.join(root, name), os.path.join(hk, name))
            print("\n图片存储" + os.path.join(hk, name))
