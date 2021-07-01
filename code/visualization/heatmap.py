import os
import numpy as np
import matplotlib.pyplot as plt

ROOT = '../../result'

def load(algorithm, benchmark_id):
    folder = os.path.join(ROOT, '{}_{}'.format(algorithm, benchmark_id))
    Fitness = []
    for name in os.listdir(folder):
        path = os.path.join(folder, name)
        if 'pair' in name:
            Fitness.append(np.load(path))
    return np.array(Fitness)

def plot_pair(data):
    mean = np.mean(data[:, :], axis=0)
    mean = np.mean(mean, axis=0)
    mean = mean - (mean - np.mean(mean)) * np.eye(2)
    plt.imshow(mean, cmap='hot')

def main():
    algorithm = 'mabmfea'
    benchmark_id = 1

    pair_data = load(algorithm, benchmark_id)

    plot_pair(pair_data)
    plt.show()

if __name__ == '__main__':
    main()
