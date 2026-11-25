import pathlib, urllib.request, time, zipfile, tarfile, json
out = pathlib.Path('third-party-sources')
out.mkdir(exist_ok=True)
archives = [
 ('NewPipeExtractor-v0.26.5','TeamNewPipe/NewPipeExtractor','v0.26.5'),
 ('InnerTubeX-v0.11.2','MetrolistGroup/innertubex','v0.11.2'),
 ('jellyfin-media-v1.2.1+1','jellyfin/jellyfin-androidx-media','v1.2.1+1'),
 ('FFmpeg','FFmpeg/FFmpeg','133069b4340961a1b3e8562a0dabfc61b35af84c'),
 ('AndroidX-Media','androidx/media','b930b40a16c06318e43c81771fa2b1024bdb3f29'),
 ('soxr-0.1.3','chirlu/soxr','0.1.3'),
 ('oboe-1.10.0','google/oboe','1.10.0'),
]
manifest=[]
for name, repo, ref in archives:
    url = f'https://codeload.github.com/{repo}/tar.gz/{ref}'
    target = out / (name + '.tar.gz')
    for attempt in range(3):
        try:
            urllib.request.urlretrieve(url,target)
            with tarfile.open(target) as archive:
                for member in archive:
                    leaf=pathlib.PurePosixPath(member.name).name.lower()
                    if member.isfile() and leaf.startswith(('license','copying','notice','copyright')):
                        safe = member.name.replace('/','_')
                        (out / (name + '__' + safe)).write_bytes(archive.extractfile(member).read())
            break
        except Exception:
            if attempt == 2: raise
            time.sleep(3)
    manifest.append(dict(repository=repo,ref=ref,url=url))
(out/'SOURCE_MANIFEST.json').write_text(json.dumps(manifest,indent=2))
with zipfile.ZipFile('release-assets/ThirdPartySources.zip','w',zipfile.ZIP_DEFLATED) as z:
    for f in out.iterdir(): z.write(f,f.name)
with zipfile.ZipFile('release-assets/DependencyMetadata.zip','w',zipfile.ZIP_DEFLATED) as z:
    for f in pathlib.Path('distribution-metadata').rglob('*'):
        if f.is_file(): z.write(f,str(f.relative_to('distribution-metadata')))
