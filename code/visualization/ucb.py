import os
import argparse
import numpy as np
import matplotlib.pyplot as plt

def get_args():
    # create argument parser
    parser = argparse.ArgumentParser()
    # parameter for problem
    parser.add_argument('--seed', type=int, default=1)
    parser.add_argument('--benchmark_id', type=int, default=0)
    parser.add_argument('--rmp', type=float, default=0.3)
    # parse args
    args = parser.parse_args()
    # add other args
    return args

ROOT = '../../result'

def load(args):
    folder = os.path.join(ROOT, '{}/{}_{}'.format(args.benchmark_id, args.algorithm, args.rmp))
    Fitness = []
    for name in os.listdir(folder):
        path = os.path.join(folder, name)
        if 'ucb' in name:
            y = np.load(path)
            Fitness.append(y)
    return np.array(Fitness)

def get_label(args):
    return '{}_{}'.format(args.algorithm, args.benchmark_id)

def plot(Fitness, args):
    cs = [
        ['b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b'],
        ['b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b'],
        ['b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b'],
        ['b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b'],
        ['r', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b'],
        ['b', 'r', 'b', 'b', 'b', 'b', 'b', 'b', 'b'],
        ['b', 'b', 'r', 'r', 'b', 'b', 'b', 'b', 'b'],
        ['b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b'],
        ['b', 'b', 'b', 'r', 'b', 'b', 'b', 'b', 'b'],
        ['b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b'],
    ]

    label = get_label(args)
    Fitness = Fitness[:, :, args.source]
    mean_fitness = np.mean(Fitness, axis=0)
    i = 0
    for target in range(mean_fitness.shape[1]):
        if target != args.source:
            plt.plot(mean_fitness[:, target], label='T{}'.format(target+1), color=cs[args.source][i], linewidth=0.3)
            plt.ylabel('UCB value')
            i += 1

def main():
    # get args
    args = get_args()

    # plot each algorithm
    args.algorithm = 'MTO'
    Fitness = load(args)
    for source in range(10):
        args.source = source
        plot(Fitness, args)

        plt.legend()
        plt.ylim((0, 2))
        plt.savefig('plot/ucb/{}.eps'.format(source + 1), dpi=300)
        plt.savefig('plot/ucb/{}.png'.format(source + 1), dpi=300)
        plt.clf()
        plt.cla()


if __name__ == '__main__':
    main()
