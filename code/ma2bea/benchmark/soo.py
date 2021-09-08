from .functions import *
import numpy as np

u1 = np.full(50, 100)
l1 = np.full(50, -100)
o1 = np.full(50, 0)
def f1(x):
    return sphere(x * (u1 - l1) + l1 - o1)

u2 = np.full(50, 100)
l2 = np.full(50, -100)
o2 = np.full(50, 80)
def f2(x):
    return sphere(x * (u2 - l2) + l2 - o2)

u3 = np.full(50, 100)
l3 = np.full(50, -100)
o3 = np.full(50, -80)
def f3(x):
    return sphere(x * (u3 - l3) + l3 - o3)

u4 = np.full(25, 0.5)
l4 = np.full(25, -0.5)
o4 = np.full(25, -0.4)
def f4(x):
    return weierstrass(x[:25] * (u4 - l4) + l4 - o4)

u5 = np.full(50, 50)
l5 = np.full(50, -50)
o5 = np.full(50, 0)
def f5(x):
    return rosenbrock(x * (u5 - l5) + l5 - o5 + 1)

u6 = np.full(50, 50)
l6 = np.full(50, -50)
o6 = np.full(50, 40)
def f6(x):
    return ackley(x * (u6 - l6) + l6 - o6)

u7 = np.full(50, 0.5)
l7 = np.full(50, -0.5)
o7 = np.full(50, -0.4)
def f7(x):
    return weierstrass(x * (u7 - l7) + l7 - o7)

u8 = np.full(50, 500)
l8 = np.full(50, -500)
o8 = np.full(50, 420.9687)
def f8(x):
    # return schwefel(x * (u8 - l8) + l8 - o8)
    return schwefel(x * (u8 - l8) + l8)

u9 = np.full(50, 100)
l9 = np.full(50, -100)
o9 = np.concatenate([np.full(25, -80), np.full(25, 80)])
def f9(x):
    return griewank(x * (u9 - l9) + l9 - o9)

u10 = np.full(50, 50)
l10 = np.full(50, -50)
o10 = np.concatenate([np.full(25, 40), np.full(25, -40)])
def f10(x):
    return rastrigin(x * (u10 - l10) + l10 - o10)

