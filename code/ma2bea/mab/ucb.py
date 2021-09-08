import warnings
import numpy as np

warnings.filterwarnings('ignore')

class UCB:

    def __init__(self, k, K, horizon=3000):
        self.k = k
        self.K = K
        self.horizon = horizon
        self.rewards = []
        self.actions = []
        self.Q = np.full([K], 0.5)
        self.N = np.zeros([K])

    @property
    def t(self):
        return len(self.actions)

    @property
    def value(self):
        Q, N, t, k, K = self.Q, self.N, self.t, self.k, self.K
        if t == 0:
            value = np.full([K], np.inf)
        else:
            value = Q + (1 + np.log(t) ** 2) / N
        return value

    def select_action(self):
        value, k, K = self.value, self.k, self.K

        K = len(value)
        p = value - np.min(value)
        if sum(p) == 0:
            p = np.ones(K)
        idx = np.arange(K)
        idx = np.concatenate([idx[:k], idx[k+1:]])
        p   = np.concatenate([p[:k], p[k+1:]])
        p = p / np.sum(p)

        if np.isnan(p).any():
            return np.random.choice(idx)
        else:
            return np.random.choice(idx, p=p)

    def update_estimate(self, a, r):
        self.actions.append(a)
        self.rewards.append(r)
        actions = np.array(self.actions)
        rewards = np.array(self.rewards)
        self.Q[a] = np.mean(rewards[actions == a])
        self.N[a] = self.actions.count(a)


        while len(self.actions) > self.horizon:
            a = self.actions.pop(0)
            self.rewards.pop(0)
            r = rewards[actions == a]
            if len(r) == 0:
                self.Q[a] = 0.5
            else:
                self.Q[a] = np.mean(r)
            self.N[a] = self.actions.count(a)

def main():
    solver = KLMABUSolver(0, 10)
    solver.update_estimate(1, 1)
    print(solver.Q)
    print(solver.N)

if __name__ == '__main__':
    main()
