import util
import optimizer
import benchmark
import numpy as np

def main():
    # get parameters
    args = util.get_args()

    # set random seed
    np.random.seed(args.seed)

    # get benchmark
    functions = benchmark.get_functions(args)
    print(functions)

    # run optimizer
    Results = []
    mto = args.algorithm(functions, args)
    mto.optimize(callback=Results.append)

    # save data
    optimizer.save(Results, args)

if __name__ == '__main__':
    try:
        main()
    except KeyboardInterrupt:
        exit(0)
