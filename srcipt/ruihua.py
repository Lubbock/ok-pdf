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

    # 创建 核
    # kernel = np.ones((2, 2), np.uint8)
    # 腐蚀
    # erorsion_img = cv2.erode(timg, kernel, iterations=1)

    # 膨胀
    # erorsion_img = cv2.dilate(erorsion_img, kernel, iterations=1)
    # 腐蚀
    kernel = np.ones((1, 1), np.uint8)
    # img = cv2.dilate(img, kernel, iterations=2)
    img = cv2.erode(img, kernel, iterations=3)
    ret, img = cv2.threshold(img, 180, 255, cv2.THRESH_BINARY)
    img = cv2.dilate(img, kernel, iterations=2)
    kernel = np.array([[0, -1, 0], [-1, 8, -1], [0, -1, 0]], np.float32)
    img = cv2.filter2D(img, -1, kernel=kernel)
    kernel = np.array([[0, -1, 0], [-1, 7, -1], [0, -1, 0]], np.float32)
    img = cv2.filter2D(img, -1, kernel=kernel)
    cv2.imwrite(op, img, [cv2.IMWRITE_PNG_COMPRESSION, 0])


# USM锐化公式表示如下：
# dst = src1*alpha + src2*beta + gamma;
# （源图像– w*高斯模糊）/（1-w）；其中w表示权重（0.1～0.9），默认为0.6
def usm(fp, op):
    src = cv2.imread(fp)
    rows, cols, channels = src.shape
    blur_img = cv2.GaussianBlur(src, (0, 0), 5)

    blank = np.zeros([rows, cols, channels], src.dtype)
    usm = cv2.addWeighted(src, 1.5, blur_img, -0.5, 0)
    usm = cv2.addWeighted(usm, 1.2, blank, -0.4, -40)
    # kernel = np.ones((2, 1), np.uint8)
    # usm = cv2.erode(usm, kernel, iterations=1)
    # for i in range(1):
    #     blur_img = cv2.GaussianBlur(usm, (0, 0), 5)
    #     usm = cv2.addWeighted(usm, 1.5, blur_img, -0.5, 0)
    h, w = src.shape[:2]
    result = np.zeros([h, w * 2, 3], dtype=src.dtype)
    result[0:h, 0:w, :] = src
    result[0:h, w:2 * w, :] = usm
    cv2.imwrite(op, result, [cv2.IMWRITE_PNG_COMPRESSION, 0])


if __name__ == "__main__":
    hk = "/media/lame/0DD80F300DD80F30/code/ok-pdf/src/main/resources/hk"
    for root, dirs, files in os.walk("/media/lame/0DD80F300DD80F30/code/ok-pdf/src/main/resources/haskhell",
                                     topdown=False):
        for name in files:
            print("\n正在处理图片" + os.path.join(root, name))

            usm(os.path.join(root, name), os.path.join(hk, name))
            print("\n图片存储" + os.path.join(hk, name))
