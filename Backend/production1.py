import dlib
from skimage import io
from scipy.spatial import distance
import numpy as np

#предобученные модели

sp = dlib.shape_predictor('1.dat')
facerec = dlib.face_recognition_model_v1('2.dat')
detector = dlib.get_frontal_face_detector()

#первое фото

img = io.imread('test1.jpg')

#поиск лица

dets = detector(img, 1)
for k, d in enumerate(dets):
    shape = sp(img, d)
#берем дескриптор

face_descriptor1 = facerec.compute_face_descriptor(img, shape)

#Загружаем дескриптор из базы данных

face_descriptor2 = np.load('test1.npy')
#Евклидово расстояние
a = distance.euclidean(face_descriptor1, face_descriptor2)
print(a)
