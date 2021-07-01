import numpy as np
import matplotlib.pyplot as plt

FMT = [
    '-r^',
    '-bo',
    '-g+',
    '-ks',
]

fig = plt.figure()
figlegend = plt.figure()
ax = fig.add_subplot(111)
lines = ax.plot(
        range(10), np.random.randn(10), FMT[0],
        range(10), np.random.randn(10), FMT[1],
        range(10), np.random.randn(10), FMT[2],
        range(10), np.random.randn(10), FMT[3])
figlegend.legend(lines, ('Ma2BEA', 'MFEA', 'MaTGA', 'SBSGA'), 'center', ncol=4)
fig.show()
figlegend.show()
figlegend.savefig('legend.png')

plt.show()
