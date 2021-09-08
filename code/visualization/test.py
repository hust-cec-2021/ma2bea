import os
import argparse
import numpy as np
from scipy import stats
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
    args.algorithms = ['mfea', 'klmabmfea']

    return args

ROOT = '../../result'
# ROOT = '../../_/result'

def load(args):
    folder = os.path.join(ROOT, '{}/{}_{:0.1f}'.format(args.benchmark_id, args.algorithm, args.rmp))
    Fitness = []
    for name in os.listdir(folder):
        path = os.path.join(folder, name)
        if 'fitness' in name:
            y = np.load(path)
            Fitness.append(y)
    return np.array(Fitness)

def main():
    # get args
    args = get_args()

    algorithms = ['mfea', 'klmabmfea']
    rmps = [0.3]

#    algorithms = ['mfea', 'mabmfea']
#    rmps = [0.5]

    for benchmark_id in range(11):
        args.benchmark_id = benchmark_id
        args.algorithm = 'mfea'
        y1 = load(args)[:, -1, :].reshape(-1)
        args.algorithm = 'klmabmfea'
        y2 = load(args)[:, -1, :].reshape(-1)
        r = stats.wilcoxon(y1, y2)
        print(benchmark_id, r.pvalue)

if __name__ == '__main__':
    main()

