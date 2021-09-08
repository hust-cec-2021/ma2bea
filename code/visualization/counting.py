import os
import argparse
import numpy as np
import matplotlib.pyplot as plt
plt.rcParams['font.family'] = 'Times New Roman'
plt.rcParams['font.size'] = 18
plt.rcParams['figure.figsize'] = 8, 4

ROOT = '../../result'

def get_args():
    # create argument parser
    parser = argparse.ArgumentParser()
    # parameter for problem
    parser.add_argument('--benchmark_id', type=int, default=0)
    parser.add_argument('--rmp', type=float, default=0.3)
    # parse args
    args = parser.parse_args()
    # add other args
    args.algorithm = 'MTO'
    return args

def load(args):
    folder = os.path.join(ROOT, '{}/{}_{:0.1f}'.format(args.benchmark_id, args.algorithm, args.rmp))
    Fitness = []
    for name in os.listdir(folder):
        path = os.path.join(folder, name)
        if 'pair' in name:
            y = np.load(path)
            Fitness.append(y)
    return np.array(Fitness)

def plot_pair(data, k):
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

    mean = np.mean(data[:, :, k, :], axis=0)
    mean = np.mean(mean, axis=0)
    x = []
    y = []
    for j in range(10):
        if j != k:
            x.append('T{}'.format(j + 1))
            y.append(mean[j])
    plt.bar(x, y, color=cs[k])
    # plt.title('Task {}'.format(k+1))
    plt.ylabel('average knowledge transfer times')
    # plt.xlabel('knowledge transfer target')
    # plt.ylim((1, 3.5))
    plt.savefig('plot/count/{}.eps'.format(k + 1), dpi=300)
    plt.savefig('plot/count/{}.png'.format(k + 1), dpi=300)
    plt.clf()
    plt.cla()

def main():
    # get args
    args = get_args()
    pair_data = load(args)
    for k in range(10):
        plot_pair(pair_data, k)

if __name__ == '__main__':
    main()
