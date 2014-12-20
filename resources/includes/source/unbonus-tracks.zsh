#!/usr/bin/zsh

IFS=$'\n';

suffix=" (Bonus Tracks Version)";

for songpath in $(mpc search album "$suffix"); do
    echo $songpath;
    tag=`metaflac --show-tag=ALBUM "/mnt/quickie/Music/$songpath"`;
    metaflac --remove-tag=ALBUM "/mnt/quickie/Music/$songpath";
    metaflac --set-tag="ALBUM=${${${(C)tag#ALBUM=}%$suffix}/The /the }" \
        "/mnt/quickie/Music/$songpath";
done
