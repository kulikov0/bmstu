#Основной скрипт

import glob
import os
import dlib
from skimage import io
from scipy.spatial import distance
import numpy as np

#Neural networks
sp = dlib.shape_predictor('1.dat')
facerec = dlib.face_recognition_model_v1('2.dat')
detector = dlib.get_frontal_face_detector()


#Search image in directory
filename_list = glob.glob('*.jpeg')

#Open this image
for filename in filename_list:
    with open(filename, 'rb') as f:
        img = io.imread(filename)

#Detect face
dets = detector(img, 1)
for k, d in enumerate(dets):
    shape = sp(img, d)

#Take descriptor
face_descriptor1 = facerec.compute_face_descriptor(img, shape)

#Release memory
#os.remove(filename)

#Download descriptor from database
database_list = glob.glob('*.npy')

for database in database_list:
    b = 0
    face_descriptor2 = np.load(database)
    #Count Euclidean distance
    a = distance.euclidean(face_descriptor1, face_descriptor2)
    if a > b:
        b = a
        c = str(database)

print(b,c)




