import numpy as np


def sphere(x):
    return np.sum(np.power(x, 2))

def griewank(x):
    D = x.shape[0]
    return np.sum(np.power(x, 2) / 4000) - \
        np.prod(np.cos(x / np.sqrt(np.linspace(1, D, D)))) + 1


def rastrigin(x):
    return np.sum(np.power(x, 2) - 10 * np.cos(2 * np.pi * x) + 10)


def schwefel(x):
    D = x.shape[0]
    return 418.9829 * D - np.sum(x * np.sin(np.power(np.abs(x), 0.5)))


def rosenbrock(x):
    return np.sum(
        100 * np.power(x[:-1] **  2 - x[1:], 2) + \
        (x[:-1] - 1) ** 2)

def ackley(x):
    return -20 * np.exp(-0.2 * np.sqrt(np.mean(np.power(x, 2)))) - \
        np.exp(np.mean(np.cos(2 * np.pi * x))) + 20 + np.exp(1)


# Weierstrass function
def get_weierstrass_const(D, a=0.5, b=3, kmax=20):
    return np.sum([a ** k * np.cos(2 * np.pi * b ** k * 0.5) for k in range(kmax)])

weierstrass_const = {
    2: get_weierstrass_const(2),
    25: get_weierstrass_const(25),
    50: get_weierstrass_const(50),
}
def weierstrass(x, a=0.5, b=3, kmax=20):
    D = x.shape[0]
    return np.sum(np.stack([a ** k * np.cos(2 * np.pi * b ** k * (x + 0.5))
                            for k in range(kmax)])) - D * weierstrass_const[D]

u1 = np.full(2, 100)
l1 = np.full(2, -100)
o1 = np.full(2, 0)
def f1(x):
    return sphere(x * (u1 - l1) + l1 - o1)

u2 = np.full(2, 100)
l2 = np.full(2, -100)
o2 = np.full(2, 80)
def f2(x):
    return sphere(x * (u2 - l2) + l2 - o2)

u3 = np.full(2, 100)
l3 = np.full(2, -100)
o3 = np.full(2, -80)
def f3(x):
    return sphere(x * (u3 - l3) + l3 - o3)

u4 = np.full(25, 0.5)
l4 = np.full(25, -0.5)
o4 = np.full(25, -0.4)
def f4(x):
    return weierstrass(x[:25] * (u4 - l4) + l4 - o4)

u5 = np.full(2, 50)
l5 = np.full(2, -50)
o5 = np.full(2, 0)
def f5(x):
    return rosenbrock(x * (u5 - l5) + l5 - o5 + 1)

u6 = np.full(2, 50)
l6 = np.full(2, -50)
o6 = np.full(2, 0)
def f6(x):
    return ackley(x * (u6 - l6) + l6 - o6)

u7 = np.full(2, 0.5)
l7 = np.full(2, -0.5)
o7 = np.full(2, 0)
def f7(x):
    return weierstrass(x * (u7 - l7) + l7 - o7)

u8 = np.full(2, 500)
l8 = np.full(2, -500)
o8 = np.full(2, 420.9687)
def f8(x):
    # return schwefel(x * (u8 - l8) + l8 - o8)
    return schwefel(x * (u8 - l8) + l8)

u9 = np.full(2, 100)
l9 = np.full(2, -100)
# o9 = np.concatenate([np.full(1, -80), np.full(1, 80)])
o9 = np.full(2, 0)
def f9(x):
    return griewank(x * (u9 - l9) + l9 - o9)

u10 = np.full(2, 5)
l10 = np.full(2, -5)
# o10 = np.concatenate([np.full(1, 40), np.full(1, -40)])
o10 = np.full(2, 0)
def f10(x):
    return rastrigin(x * (u10 - l10) + l10 - o10)

from pylab import meshgrid,cm,imshow,contour,clabel,colorbar,axis,title,show
from matplotlib.ticker import LinearLocator, FormatStrFormatter
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
from matplotlib import cm

def f_wrap(f, X, Y):
    Z = np.zeros_like(X)
    for i in range(len(X)):
        for j in range(len(Y)):
            Z[i, j] = f(np.array([X[i,j], Y[i,j]]))
    return Z

def plot(f):
    x = np.arange(0, 1, 0.01)
    y = np.arange(0, 1, 0.01)
    X, Y = meshgrid(x, y) # grid of point
    Z = f_wrap(f, X, Y) # evaluation of the function on the grid

    fig = plt.figure()
    ax = fig.gca(projection='3d')
    surf = ax.plot_surface(X, Y, Z, rstride=1, cstride=1, 
                          cmap=cm.RdBu,linewidth=0, antialiased=False)

    ax.zaxis.set_major_locator(LinearLocator(10))
    ax.zaxis.set_major_formatter(FormatStrFormatter('%.02f'))

    fig.colorbar(surf, shrink=0.5, aspect=5)
    plt.tight_layout()
    plt.show()

def main():
    plot(f7)


if __name__ == '__main__':
    main()