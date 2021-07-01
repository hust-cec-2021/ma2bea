import pandas as pd
import matplotlib.pyplot as plt

plt.rcParams['font.size'] = 10

hatch = ['', '', '', '.']

def load():
    path = '../../result/runtime.csv'
    df = pd.read_csv(path, header=0)
    return df

def plot(fig, i, row):
    name = '{}-task benchmark'.format(int(row['benchmark']))
    Y = float(row['mfea']), float(row['ebsga']), float(row['matga']), float(row['propose'])
    X = ['MFEA', 'SBSGA', 'MaTGA', 'Ma2BEA']
    ax = fig.add_subplot(1, 2, i + 1)
    for j, (x, y) in enumerate(zip(X, Y)):
        ax.bar(x, y, color='white', edgecolor='black', hatch=hatch[j])
        if i == 0:
            plt.ylim((0.9, 1.8))
            ax.text(j - 0.4, y + 0.05, '{:0.3f}'.format(y), color='black')
        elif i == 1:
            plt.ylim((0.7, 3.7))
            ax.text(j - 0.4, y + 0.1, '{:0.3f}'.format(y), color='black')
    ax.set_title(name)

def main():
    fig = plt.figure()
    df = load()
    for i, row in df.iterrows():
        plot(fig, i, row)
    plt.show()

if __name__ == '__main__':
    main()
