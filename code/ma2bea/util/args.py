import argparse
import optimizer

def get_args():
    # create argument parser
    parser = argparse.ArgumentParser()

    # parameter for problem
    parser.add_argument('--seed', type=int, default=1)
    parser.add_argument('--algorithm', type=str, default='mto', choices=['mto'])
    parser.add_argument('--benchmark_id', type=int, default=0)

    # parameter for optimizer
    parser.add_argument('--pop_size', type=int, default=100)
    parser.add_argument('--num_iter', type=int, default=1000)
    parser.add_argument('--dimension', type=int, default=50)
    parser.add_argument('--sbxdi', type=int, default=2)
    parser.add_argument('--pmdi', type=int, default=5)
    parser.add_argument('--pswap', type=float, default=0.5)
    parser.add_argument('--rmp', type=float, default=0.3)

    # parameter for logging
    parser.add_argument('--log_iter', type=int, default=10)

    # parse args
    args = parser.parse_args()

    # assign algorithm
    if args.algorithm == 'mto':
        args.algorithm = optimizer.MTO
    else:
        raise NotImplementedError

    return args

