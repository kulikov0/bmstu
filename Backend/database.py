import dlib
from skimage import io
import numpy as np
img = io.imread('test.jpg')

dets_webcam = detector(img, 1)
for k, d in enumerate(dets_webcam):
    shape = sp(img, d)

face_descriptor1 = facerec.compute_face_descriptor(img, shape)
np.save("database/test.npy", face_descriptor1)
