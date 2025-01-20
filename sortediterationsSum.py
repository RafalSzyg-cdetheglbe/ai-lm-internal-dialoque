import pandas as pd
import os

file_path = 'psychologyrating.csv'
data = pd.read_csv(file_path)

columns_to_sum = ['Cohesion', 'Mistakes', 'Characters', 'Emotion', 'Engagement']
data['Sum'] = data[columns_to_sum].sum(axis=1)

data = data.rename(columns={'Sum': 'suma'})

sorted_data = data[['Id', 'suma']].sort_values(by='suma', ascending=False)

mean_values = data[columns_to_sum].mean()

output_folder = 'wygrany'
if not os.path.exists(output_folder):
    os.makedirs(output_folder)

sorted_data_file = os.path.join(output_folder, 'sorted_results.csv')
sorted_data.to_csv(sorted_data_file, index=False)

mean_data = mean_values.reset_index()
mean_data.columns = ['Argument', 'Średnia']

mean_data_file = os.path.join(output_folder, 'mean_results.csv')
mean_data.to_csv(mean_data_file, index=False)

print(f"Wyniki posortowane zostały zapisane w pliku: {sorted_data_file}")
print(f"Wyniki średnie zostały zapisane w pliku: {mean_data_file}")
