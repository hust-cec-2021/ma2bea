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
    25: get_weierstrass_const(25),
    50: get_weierstrass_const(50),
}
def weierstrass(x, a=0.5, b=3, kmax=20):
    D = x.shape[0]
    return np.sum(np.stack([a ** k * np.cos(2 * np.pi * b ** k * (x + 0.5))
                            for k in range(kmax)])) - D * weierstrass_const[D]
