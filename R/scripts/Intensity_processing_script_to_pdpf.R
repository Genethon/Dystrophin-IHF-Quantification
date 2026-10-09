
#wd=choose.dir(default = "", caption = "Selectionner le dossier du projet QuPath")
setwd("T:/images_ddc/SCANS-Axioscan/Jerome_Axioscan/25-102_IHF_SGCA_Lam_TA_Ga/QuPath_Proj_25-102_sgca_lam/Seuil9XSTD/")
setwd("T:/IM_Evaluation_preclinique/1.PROJETS/1MDYF/2.ETUDES_NON_CLINIQUES/1MDYF_GNT005_Rats_pour_papier_Lam_DysB/QuPath_Proj_GNT005_papier/")
#setwd(wd)

filenames = dir(path = "./InfDiam_Centro_results/",pattern = ".txt")
filenames = dir(path = "./InfDiam_Centro_results_Neg/",pattern = ".txt")

paths = paste0("./InfDiam_Centro_results/",filenames)
paths = paste0("./InfDiam_Centro_results_Neg/",filenames)


files = lapply(paths,read.delim)
df = NULL
for(i in 1:length(files)){
  df = rbind(df,files[[i]])
}

colnames(df)
table(df$Classification)

df2 = df[(df$Classification == "membrane_Centro"|df$Classification == "membrane_Normale"),
         c("Image", "Classification","ROI..2.00.µm.per.pixel..AF594..Mean","ROI..2.00.µm.per.pixel..AF594..Std.dev.")
]

df3 = df2[!is.na(df2$ROI..2.00.µm.per.pixel..AF594..Mean),]
t=df3$ROI..2.00.µm.per.pixel..AF594..Mean
h<-hist(t,breaks=1000,xlim=c(0,25000),border="green")
hist(t,breaks=1000,xlim=c(0,40000),border="blue")
plot(h,abline(v=threshold))

med = median(t)
mad = mad(t)
threshold=med+5*mad
print(threshold)

#Thres=c("seuil",threshold)
hist(t, breaks = 2000,main=threshold)

df2b= df2[!is.na(df2$ROI..2.00.µm.per.pixel..AF594..Mean),]
df2b$pos = ifelse(df2b$ROI..2.00.µm.per.pixel..AF594..Mean>threshold,TRUE,FALSE)

resultats <- aggregate(cbind(pos, 1) ~ Image, data = df2b, sum)

names(resultats)[2:3] <- c("ratio_pos_true", "nombre_total")
resultats$pourcentage = resultats$ratio_pos_true / resultats$nombre_total *100
write.csv2(resultats,paste("export_GNT005_madx5_Thres_", as.character(round(threshold, digits=2)),".csv"))
#write.csv2(Thres,"Valeur_Seuil_EDL_DysB.csv")
