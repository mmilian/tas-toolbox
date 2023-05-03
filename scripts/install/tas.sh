#!/usr/bin/env bash

set -euo pipefail

mkdir -p ~/.local/bin
local_bin_dir="~/.local/bin"
install_dir=$local_bin_dir
download_dir=/tmp

latest_release="latest"

download_url="https://github.com/mmilian/releases/download/v$latest_release/tas-$latest_release.tar.gz"
download_file="tas-$latest_release.tar.gz"

cd "$download_dir"
echo -e "Downloading $download_url."
curl -o "$download_file" -sL "$download_url"
tar -xzf "$download_file"
rm "$download_file"

cd "$install_dir"
if [ -f tas ]; then
    echo "Moving $install_dir/tas to $install_dir/tas.old"
fi

mv -f "$download_dir/tas" "$PWD/tas"

echo "Successfully installed `tas` in $install_dir."


