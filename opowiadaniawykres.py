import pandas as pd
import matplotlib.pyplot as plt

file_path = 'psychologyrating.csv'
data = pd.read_csv(file_path)

mean_values = data[['Cohesion', 'Mistakes', 'Characters', 'Emotion', 'Engagement']].mean()

print("\nŚrednie wartości pomiarów:")
print(mean_values)

columns_to_plot = ['Cohesion', 'Mistakes', 'Characters', 'Emotion', 'Engagement']

fig, axes = plt.subplots(3, 2, figsize=(14, 12))

axes = axes.flatten()

for i, column in enumerate(columns_to_plot):
    axes[i].plot(data['Id'], data[column], marker='o', linestyle='-', label=column)
    axes[i].set_title(column, fontsize=14)
    axes[i].set_xlabel('Iteracja', fontsize=12)
    axes[i].set_ylabel('Ocena', fontsize=12)
    axes[i].grid(True, linestyle='--', alpha=0.7)
    #axes[i].legend(title="Pomiar", fontsize=10)
    
    axes[i].set_yticks(range(int(data[column].min()), int(data[column].max()) + 1, 1))

fig.delaxes(axes[5])

plt.tight_layout()
plt.show()
