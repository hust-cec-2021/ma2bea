import matplotlib.pyplot as plt
import numpy as np
import os

def main():
    datasets = ['hopper-gravity', 'hopper-size']
    algorithms = ['MFEA', 'MFEA', 'MTO']
    rmps = [0.0, 0.3, 0.3]

    for dataset in datasets:
        print(f'[+] dataset:{dataset}')
        data = []
        for algorithm, rmp in zip(algorithms, rmps):
            path = os.path.join('../result', dataset, f'{algorithm}_{rmp}', 'fitness-1.npy')
            Y = np.load(path)
            # data.append(-Y[-1, :])
            if algorithm == 'MFEA':
                data.append(-Y[-10, :])
            else:
                data.append(-Y[-1, :])
        data = np.array(data).T
        fig, axes = plt.subplots(1, 1, sharex=True, figsize=(5, 4), tight_layout=True)
        plt.boxplot(data, labels=['EA', 'MFEA', 'Ma2BEA'])
        plt.xlabel('Algorithm')
        plt.ylabel('Total rewards')
        plt.tight_layout()
        plt.savefig(f'{dataset}.eps')
        plt.savefig(f'{dataset}.png')

if __name__ == '__main__':
    main()