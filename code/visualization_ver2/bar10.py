import os
import numpy as np
import matplotlib.pyplot as plt
plt.rcParams['font.size'] = 24

folder = '../../result/0/MTO_0.3/'
result_folder = '../../paper/figure/experiment/bar10'
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

def load():
    results = []
    for name in os.listdir(folder):
        path = os.path.join(folder, name)
        if 'pair' in name:
            results.append(np.load(path))
    return np.array(results)

def plot(data, k):
    mean = np.mean(data[:, :, k, :], axis=0)
    mean = np.mean(mean, axis=0)
    x = []
    y = []
    for j in range(10):
        if j != k:
            x.append('{}'.format(j + 1))
            y.append(mean[j])
    plt.bar(x, y, color=cs[k])
    # plt.title('Task {}'.format(k+1))
    plt.ylabel('# assortative mating')
    # plt.xlabel('knowledge transfer target')
    # plt.ylim((1, 3.5))
    plt.savefig('{}/{}.eps'.format(result_folder, k + 1), dpi=300)
    plt.savefig('{}/{}.png'.format(result_folder, k + 1), dpi=300)
    plt.clf()
    plt.cla()

def main():
    results = load()
    for k in range(10):
        plot(results, k)

if __name__ == '__main__':
    main()
