import os
import time
import argparse

def get_args():
    # create argument parser
    parser = argparse.ArgumentParser()

    # parameter for problem
    parser.add_argument('--start_seed', type=int, default=0)
    parser.add_argument('--end_seed', type=int, default=29)
    parser.add_argument('--benchmark_id', type=int, default=0)

    # parse args
    args = parser.parse_args()
    return args

def main():
    # get args
    args = get_args()

    # time estimate
    count = 0
    num_instance = args.end_seed - args.start_seed

    # loop
    for seed in range(args.start_seed, args.end_seed + 1):
        cmd = 'python3 main.py --seed={} --benchmark_id={}' \
            .format(seed,
                    args.benchmark_id)
        print(cmd)

        # run command and measure time
        tic = time.time()
        os.system(cmd)
        toc = time.time()

        # give time estimation
        count += 1
        remaining = (num_instance - count) * (toc - tic) / 3600
        print('[+] remaining: {:0.1f} hour(s)'.format(remaining))

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        exit(0)

