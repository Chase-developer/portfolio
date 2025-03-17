document.addEventListener("DOMContentLoaded", async () => {
    const fontMap = {
        "CeraProRegular": { family: "CeraPro", weight: "400", style: "normal" },
        "HKGroteskRegular": { family: "HKGrotesk", weight: "400", style: "normal" }
    };

    for (const [baseFileName, fontDetails] of Object.entries(fontMap)) {
        try {
            // Fetch URLs for both WOFF2 and WOFF
            const responseWoff2 = await fetch(`/fonts/${baseFileName}2`);
            const fontUrlWoff2 = await responseWoff2.text();

            const responseWoff = await fetch(`/fonts/${baseFileName}`);
            const fontUrlWoff = await responseWoff.text();

            // Create a FontFace with both formats, letting the browser choose
            const fontFace = new FontFace(fontDetails.family, 
                `url(${fontUrlWoff2}) format('woff2'), url(${fontUrlWoff}) format('woff')`, 
                {
                    weight: fontDetails.weight,
                    style: fontDetails.style
                });

            await fontFace.load();
            document.fonts.add(fontFace);

            console.log(`${fontDetails.family} (${fontDetails.weight}, ${fontDetails.style}) loaded successfully!`);
        } catch (error) {
            console.error(`Failed to load font ${fontDetails.family}:`, error);
        }
    }
});

