import pandas as pd
import matplotlib.pyplot as plt
from sklearn.metrics import roc_curve, auc

# -------- Threholds --------------
def apply_top_k(df_group, col_score, k=5):
    # marca como 1 a los K elementos con mayor score
    df_group = df_group.sort_values(by=col_score, ascending=False)
    df_group['Paso_Corte'] = 0
    df_group.iloc[:k, df_group.columns.get_loc('Paso_Corte')] = 1
    return df_group

def apply_fixed(df_group, col_score, threshold=0.5):
    # marca como 1 a todos los que superen un valor fijo
    df_group['Paso_Corte'] = (df_group[col_score] >= threshold).astype(int)
    return df_group

def apply_gap(df_group, col_score, percent=0.15):
    # corta cuando el salto entre scores es mayor al % del primero
    df_group = df_group.sort_values(by=col_score, ascending=False).copy()
    df_group['Paso_Corte'] = 0
    
    if not df_group.empty and df_group[col_score].iloc[0] > 0:
        primer_score = df_group[col_score].iloc[0]
        umbral_salto = primer_score * percent
        
        scores = df_group[col_score].values
        paso = [1] # el primero siempre pasa si es > 0
        cortado = False
        for i in range(1, len(scores)):
            if not cortado and (scores[i-1] - scores[i]) > umbral_salto:
                cortado = True
            paso.append(0 if cortado else 1)
        df_group['Paso_Corte'] = paso
    return df_group

# ---------------- Parametros --------------
archivo_csv = 'main_roc_result.csv'
algoritmos = [
    'AdamicAdar', 'CoeficienteDeJaccard', 'CommonNeighbors', 
    'HubPromoted', 'HubDepressed', 'PreferentialAttachment', 
    'ResourceAllocation', 'Sorensen', 'Katz', 'SimRank'
]
# parametros de los thresholds
K_VAL = 5
FIXED_VAL = 0.2
GAP_PERCENT = 0.15

# ---------------- generacion del graficos -----------------
df = pd.read_csv(archivo_csv, sep=';')
df.columns = df.columns.str.strip()

plt.figure(figsize=(12, 8))
cmap = plt.get_cmap('tab10')

for i, alg in enumerate(algoritmos):
    if alg not in df.columns:
        continue
    
    color = cmap(i)
    df[alg] = pd.to_numeric(df[alg], errors='coerce').fillna(0)
    
    # normalizar
    min_val = df[alg].min()
    max_val = df[alg].max()
    
    if max_val - min_val > 0:
        df[alg] = (df[alg] - min_val) / (max_val - min_val)

    # para dibujar la curva roc
    fpr, tpr, _ = roc_curve(df['Realidad'], df[alg])
    roc_auc = auc(fpr, tpr)
    plt.plot(fpr, tpr, color=color, lw=2, label=f'{alg} (AUC={roc_auc:.2f})')

    #para calcular los puntos de los thresholds
    estrategias = [
        ('Punto de Corte', apply_gap, 'D', {'percent': GAP_PERCENT}),
        ('TopK', apply_top_k, 'o', {'k': K_VAL}),
        ('Fijo', apply_fixed, 's', {'threshold': FIXED_VAL})
    ]

    for name, func, marker, params in estrategias:
        # Aplicamos la función a cada grupo de Nodos
        res = df.groupby('Node', group_keys=False).apply(lambda x: func(x, alg, **params))
        
        # Calculamos TPR y FPR del punto resultante
        tp = ((res['Paso_Corte'] == 1) & (res['Realidad'] == 1)).sum()
        fp = ((res['Paso_Corte'] == 1) & (res['Realidad'] == 0)).sum()
        fn = ((res['Paso_Corte'] == 0) & (res['Realidad'] == 1)).sum()
        tn = ((res['Paso_Corte'] == 0) & (res['Realidad'] == 0)).sum()
        
        tpr_p = tp / (tp + fn) if (tp + fn) > 0 else 0
        fpr_p = fp / (fp + tn) if (fp + tn) > 0 else 0
        
        # referencias de las tecnias de lp
        plt.plot(fpr_p, tpr_p, marker=marker, markersize=9, color=color, 
                 markeredgecolor='black', markeredgewidth=1, alpha=0.9)

# referencias de los puntos de los thresholds
plt.plot([], [], 'kD', label='Threshold: Punto de corte: 0.15', markersize=8)
plt.plot([], [], 'ko', label='Threshold: Top-K: 5', markersize=8)
plt.plot([], [], 'ks', label='Threshold: Umbral Fijo: 0.2', markersize=8)

plt.plot([0, 1], [0, 1], 'k--', alpha=0.5) # Línea diagonal de azar
plt.xlabel('False Positive Rate (FPR)')
plt.ylabel('True Positive Rate (TPR)')
plt.title('Comparativa de Técnicas LP y Estrategias de Corte')
plt.legend(loc='lower right', fontsize='small', ncol=2)
plt.grid(alpha=0.3)
plt.tight_layout()
plt.savefig('curva_roc.png', dpi=300, bbox_inches='tight')
plt.show()