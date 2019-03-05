insert into disease_service.ds_synonym(disease_name, ds_disease_id, created_at, updated_at, source_name)
select * from (
select '3-methylglutaconic aciduria type 1', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='3-methylglutaconic aciduria 1'
 union
select '3-methylglutaconic aciduria type 3', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='3-methylglutaconic aciduria 3'
 union
select '3-methylglutaconic aciduria type 5', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='3-methylglutaconic aciduria 5'
 union
select '3-methylglutaconic aciduria type viii', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='3-methylglutaconic aciduria 8'
 union
select '3-methylglutaconic aciduria type ix', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='3-methylglutaconic aciduria 9'
 union
select 'aland island eye disease', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='aaland island eye disease'
 union
select 'ablepharon macrostomia syndrome', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='ablepharon-macrostomia syndrome'
 union
select 'acatalasia', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='acatalasemia'
 union
select 'acrofacial dysostosis cincinnati type', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='acrofacial dysostosis, cincinnati type'
 union
select 'agenesis of the corpus callosum with peripheral neuropathy', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='agenesis of the corpus callosum, with peripheral neuropathy'
 union
select 'albright''s hereditary osteodystrophy', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='albright hereditary osteodystrophy'
 union
select 'alpha 1-antitrypsin deficiency', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='alpha-1-antitrypsin deficiency'
 union
select 'alpha thalassemia', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='alpha-thalassemia'
 union
select 'alzheimer''s disease', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='alzheimer disease'
 union
select 'alzheimer''s disease 1', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='alzheimer disease 1'
 union
select 'alzheimer''s disease 18', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='alzheimer disease 18'
 union
select 'alzheimer''s disease 19', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='alzheimer disease 19'
 union
select 'alzheimer''s disease 2', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='alzheimer disease 2'
 union
select 'alzheimer''s disease 3', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='alzheimer disease 3'
 union
select 'alzheimer''s disease 4', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='alzheimer disease 4'
 union
select 'amelogenesis imperfecta type 1a', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta 1a'
 union
select 'amelogenesis imperfecta type 1b', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta 1b'
 union
select 'amelogenesis imperfecta type 1c', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta 1c'
 union
select 'amelogenesis imperfecta type 1e', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta 1e'
 union
select 'amelogenesis imperfecta type 1f', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta 1f'
 union
select 'amelogenesis imperfecta type 1g', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta 1g'
 union
select 'amelogenesis imperfecta type 1h', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta 1h'
 union
select 'amelogenesis imperfecta type 3a', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta 3a'
 union
select ' amelogenesis imperfecta type 3b', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta 3b'
 union
select 'amelogenesis imperfecta type 4', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta 4'
 union
select 'amelogenesis imperfecta hypomaturation type 2a1', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta, hypomaturation type, 2a1'
 union
select 'amelogenesis imperfecta hypomaturation type 2a2', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta, hypomaturation type, 2a2'
 union
select 'amelogenesis imperfecta hypomaturation type 2a3', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta, hypomaturation type, 2a3'
 union
select 'amelogenesis imperfecta hypomaturation type 2a4', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta, hypomaturation type, 2a4'
 union
select 'amelogenesis imperfecta hypomaturation type 2a5', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amelogenesis imperfecta, hypomaturation type, 2a5'
 union
select 'amyotrophic lateral sclerosis type 1', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 1'
 union
select 'amyotrophic lateral sclerosis type 10', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 10'
 union
select 'amyotrophic lateral sclerosis type 11', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 11'
 union
select 'amyotrophic lateral sclerosis type 12', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 12'
 union
select 'amyotrophic lateral sclerosis type 13', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 13'
 union
select 'amyotrophic lateral sclerosis type 17', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 17'
 union
select 'amyotrophic lateral sclerosis type 18', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 18'
 union
select 'amyotrophic lateral sclerosis type 19', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 19'
 union
select 'amyotrophic lateral sclerosis type 2', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 2'
 union
select 'amyotrophic lateral sclerosis type 20', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 20'
 union
select 'amyotrophic lateral sclerosis type 21', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 21'
 union
select 'amyotrophic lateral sclerosis type 23', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 23'
 union
select 'amyotrophic lateral sclerosis type 4', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 4'
 union
select 'amyotrophic lateral sclerosis type 8', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 8'
 union
select 'amyotrophic lateral sclerosis type 9', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='amyotrophic lateral sclerosis 9'
 union
select 'aromatic l-amino acid decarboxylase deficiency', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='aromatic l-amino-acid decarboxylase deficiency'
 union
select 'autoimmune lymphoproliferative syndrome type 2a', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='autoimmune lymphoproliferative syndrome 2a'
 union
select 'autoimmune lymphoproliferative syndrome type 3', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='autoimmune lymphoproliferative syndrome 3'
 union
select 'axenfeld-rieger syndrome type 1', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='axenfeld-rieger syndrome 1'
 union
select 'axenfeld-rieger syndrome type 3', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='axenfeld-rieger syndrome 3'
 union
select 'achondrogenesis type ia', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='achondrogenesis 1a'
 union
select 'achondrogenesis type ib', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='achondrogenesis 1b'
 union
select 'achondrogenesis type ii', id, now(), now(), 'Disease Ontology' from disease_service.ds_disease where lower(trim(disease_name))='achondrogenesis 2'
) as tmp;