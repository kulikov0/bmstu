import dlib
from skimage import io
from scipy.spatial import distance

#предобученные модели

sp = dlib.shape_predictor('1.dat')
facerec = dlib.face_recognition_model_v1('2.dat')
detector = dlib.get_frontal_face_detector()

#первое фото

img = io.imread('test.jpg')

#поиск лица

dets = detector(img, 1)
for k, d in enumerate(dets):
    print("Detection {}: Left: {} Top: {} Right: {} Bottom: {}".format(
        k, d.left(), d.top(), d.right(), d.bottom()))
    shape = sp(img, d)
#берем дескриптор

face_descriptor1 = facerec.compute_face_descriptor(img, shape)

#загружаем и обрабатываем второе фото

img = io.imread('test1.jpg')

dets_webcam = detector(img, 1)
for k, d in enumerate(dets_webcam):
    print("Detection {}: Left: {} Top: {} Right: {} Bottom: {}".format(
        k, d.left(), d.top(), d.right(), d.bottom()))
    shape = sp(img, d)

#выгружаем второй дескриптор

face_descriptor2 = facerec.compute_face_descriptor(img, shape)

#Евклидово расстояние между двумя лицами


a = distance.euclidean(face_descriptor1, face_descriptor2)
print(a)
