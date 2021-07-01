import os
import argparse
import numpy as np
import matplotlib.pyplot as plt

def get_args():
    # create argument parser
    parser = argparse.ArgumentParser()
    # parameter for problem
    parser.add_argument('--benchmark_id', type=int, default=0)
    parser.add_argument('--rmp', type=float, default=0.3)
    # parse args
    args = parser.parse_args()
    # add other args
    args.algorithms = ['mfea', 'klmabmfea', 'MTO']

    return args

ROOT = '../../result'

def load(args):
    folder = os.path.join(ROOT, '{}/{}_{:0.1f}'.format(args.benchmark_id, args.algorithm, args.rmp))
    Fitness = []
    for name in os.listdir(folder):
        path = os.path.join(folder, name)
        if 'fitness' in name:
            y = np.load(path)
            Fitness.append(y)
    return np.array(Fitness)

def get_label(args):
    folder = os.path.join(ROOT, '{}/{}_{:0.1f}'.format(args.benchmark_id, args.algorithm, args.rmp))
    return folder

def plot(Fitness, args):
    label = get_label(args)
    mean = np.mean(Fitness, axis=0)
    mean_ = np.mean(mean, axis=1)
#    for i in range(10):
#        mean_ = mean[:, i]
#    plt.subplot(2, 5, i + 1)
    plt.plot(mean_, label=label)
    plt.yscale('log')

def main():
    # get args
    args = get_args()

    # plot each algorithm
    for algorithm in args.algorithms:
        args.algorithm = algorithm
        Fitness = load(args)
        plot(Fitness, args)
    plt.legend()
    plt.show()

if __name__ == '__main__':
    main()
