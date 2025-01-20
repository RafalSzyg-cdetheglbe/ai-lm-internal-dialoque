import os
import pandas as pd
import matplotlib.pyplot as plt

folder_path = os.path.dirname(os.path.abspath(__file__))

for file_name in os.listdir(folder_path):
    if file_name.endswith(".csv"):
        file_path = os.path.join(folder_path, file_name)

        try:
            data = pd.read_csv(file_path)

            base_name = os.path.splitext(file_name)[0]
            base_title = base_name.replace("rating", "").replace("-", " - ").strip()

            num_columns = len(data.columns[1:])
            fig, axes = plt.subplots(1, num_columns, figsize=(5 * num_columns, 6), sharex=True)

            if num_columns == 1:
                axes = [axes]  

            for ax, column in zip(axes, data.columns[1:]):
                ax.plot(data.iloc[:, 0], data[column], label=column, marker="o")
                ax.set_title(f"{base_title} - {column}")
                ax.set_xlabel("Iteracja")
                ax.set_ylabel("Ocena")
                ax.grid(True)
                ax.set_xticks(range(int(data.iloc[:, 0].min()), int(data.iloc[:, 0].max()) + 1, 1))  # Odstępy osi X co 1
                ax.set_yticks(range(int(data[column].min()), int(data[column].max()) + 1, 1))  # Odstępy osi Y co 1
                ax.legend()

            plt.tight_layout()
            
            output_path = os.path.join(folder_path, f"{base_title}.png")
            plt.savefig(output_path)
            plt.close()
            print(f"Wykres zapisano: {output_path}")

        except Exception as e:
            print(f"Błąd przetwarzania pliku {file_name}: {e}")
