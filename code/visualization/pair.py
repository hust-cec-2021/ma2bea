import os
import argparse
import numpy as np
import matplotlib.pyplot as plt

def get_args():
    # create argument parser
    parser = argparse.ArgumentParser()
    # parameter for problem
    parser.add_argument('--seed', type=int, default=1)
    parser.add_argument('--benchmark_id', type=int, default=2)
    parser.add_argument('--source', type=int, default=0)
    # parse args
    args = parser.parse_args()
    # add other args
    return args

ROOT = '../../result'

def load(args):
    folder = os.path.join(ROOT, '{}_{}'.format(args.algorithm, args.benchmark_id))
    Fitness = []
    for name in os.listdir(folder):
        path = os.path.join(folder, name)
        if 'pair' in name:
            y = np.load(path)
            Fitness.append(y)
    return np.array(Fitness)

def get_label(args):
    return '{}_{}'.format(args.algorithm, args.benchmark_id)

def plot(Fitness, args):
    label = get_label(args)
    Fitness = Fitness[:, :, args.source]
    mean_fitness = np.mean(Fitness, axis=0)
    for target in range(mean_fitness.shape[1]):
        if target == args.source:
            plt.plot(mean_fitness[:, target], label='{}_{}'.format(label, target))

def main():
    # get args
    args = get_args()

    # plot each algorithm
    args.algorithm = 'mabmfea'
    Fitness = load(args)
    plot(Fitness, args)

    # plt.legend()
    # plt.ylim((0, 3))
    plt.show()

if __name__ == '__main__':
    main()
