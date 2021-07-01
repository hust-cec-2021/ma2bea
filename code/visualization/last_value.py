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

def last_value(data):
    mean = np.mean(data, axis=0)
    return mean[-1]

def main():
    # get args
    args = get_args()

#    algorithms = ['mfea', 'klmabmfea']
#    rmps = [0.5]

    algorithms = ['mfea', 'klmabmfea', 'MTO']
    rmps = [0.3]

    for benchmark_id in range(11):
        args.benchmark_id = benchmark_id
        for algorithm in algorithms:
            args.algorithm = algorithm
            for rmp in rmps:
                args.rmp = rmp
                data = load(args)
                mean = last_value(data)
                print('{} {} {} {}'.format(
                    benchmark_id, algorithm, rmp,
                    ' '.join('{}'.format(_) for _ in mean)))
#                print('{} {} {:0.1f} {} {} {:0.2f} {:0.2f}'.format(
#                    benchmark_id, algorithm, rmp,
#                    np.mean(mean), np.std(mean), np.min(mean), np.max(mean)))
        print()

if __name__ == '__main__':
    main()
