const canvas = document.getElementById('waveCanvas');
const ctx = canvas.getContext('2d');

let dots = [];
let waves = []; // Store active waves
const wavePool = []; // Queue of reusable waves
const waveExpiryTime = 5000; // 5 seconds before a wave is permanently discarded
const dotSpacing = 30;
const maxWaveRadius = 800; // Max travel distance before fading
const waveSpeed = 3; // How fast the wave expands
const waveFadeSpeed = 0.01; // How quickly the wave loses intensity
const baseOpacity = 0.1; // Default opacity for inactive dots
const minDotSize = 2; // Default dot size
const maxDotSize = 10; // Max dot size when affected
const growSpeed = 0.05; // How quickly dots change size
let inactivityTimer;
let isIdle = false; // Track inactivity state
let waveInterval;

function resizeCanvas() {
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    generateDots();
}

function generateDots() {
    dots = [];
    for (let x = 0; x < canvas.width; x += dotSpacing) {
        for (let y = 0; y < canvas.height; y += dotSpacing) {
            dots.push({ 
                x, 
                y, 
                opacity: baseOpacity, 
                size: minDotSize, 
                targetSize: minDotSize, 
                active: false // Initially inactive
            });
        }
    }
}

function drawDots() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = "white";

    dots.forEach(dot => {
        if (!dot.active) return; // Skip inactive dots

        ctx.beginPath();
        ctx.globalAlpha = dot.opacity;
        ctx.arc(dot.x, dot.y, dot.size, 0, Math.PI * 2);
        ctx.fill();

        // Gradually fade inactive dots back to base opacity
        if (!dot.active) dot.opacity = Math.max(baseOpacity, dot.opacity - waveFadeSpeed);

        // Smoothly transition dot size
        dot.size += (dot.targetSize - dot.size) * growSpeed;
    });

    ctx.globalAlpha = 1; // Reset transparency
}



function handleMouseMove(event) {
    isIdle = false;
    resetInactivityTimer();

    const { clientX, clientY } = event;

    // Prevent adding duplicate waves at the same position
    if (waves.length > 0) {
        const lastWave = waves[waves.length - 1];
        if (lastWave.x === clientX && lastWave.y === clientY) {
            return; // Skip adding if it's the same position
        }
    }

    // Try to reuse a wave from the pool
    let wave;
    if (wavePool.length > 0) {
        wave = wavePool.pop(); // Reuse a wave
        wave.x = clientX;
        wave.y = clientY;
        wave.radius = 0;
        wave.opacity = 1.0;
        wave.timestamp = Date.now(); // Reset timestamp
    } else {
        // No available wave, create a new one
        wave = { x: clientX, y: clientY, radius: 0, opacity: 1.0, timestamp: Date.now() };
    }

    waves.push(wave);
}

function createRandomWave() {
    if (isIdle) {
        const randomX = Math.random() * canvas.width;
        const randomY = Math.random() * canvas.height;

        let wave;
        if (wavePool.length > 0) {
            wave = wavePool.pop();
            wave.x = randomX;
            wave.y = randomY;
            wave.radius = 0;
            wave.opacity = 1.0;
            wave.timestamp = Date.now();
        } else {
            wave = { x: randomX, y: randomY, radius: 0, opacity: 1.0, timestamp: Date.now() };
        }

        waves.push(wave);
    }
}

function updateWaves() {
    const now = Date.now();

    waves.forEach(wave => {
        wave.radius += waveSpeed;
        wave.opacity -= waveFadeSpeed * 2;
		//wave.opacity -= waveFadeSpeed * (1 - wave.opacity);
    });

    // Filter out active waves, move expired ones to the pool
    waves = waves.filter(wave => {
		
        if (wave.opacity > 0) return true; // Keep active waves

        // If the wave hasn't expired, move it to the pool
        if (now - wave.timestamp < waveExpiryTime) {
            wavePool.push(wave);
        }
        
        return false; // Remove from waves list
    });
}



function applyWaveEffects() {
    dots.forEach(dot => {
        let maxEffect = 0; // Track the strongest effect from all waves
        let wasActive = dot.active;
        dot.active = false; // Reset active state

        for (const wave of waves) {
            const dx = dot.x - wave.x;
            const dy = dot.y - wave.y;
            const distanceSquared = dx * dx + dy * dy; // No sqrt
            const radiusSquared = wave.radius * wave.radius; // Square radius for comparison

            if (distanceSquared < radiusSquared) {
                dot.active = true;
                let effectStrength = wave.opacity * (1 - distanceSquared / radiusSquared); // Stronger near wave center
                maxEffect = Math.max(maxEffect, effectStrength);
            }
        }

        if (dot.active) {
            // Apply max effect to opacity and size
            dot.opacity = Math.min(1, baseOpacity + maxEffect);
            dot.targetSize = minDotSize + maxEffect * (maxDotSize - minDotSize);
        } else if (wasActive) {
            // If it was active but now isn't, start fading it out
            dot.targetSize = minDotSize;
        }
    });
}



function resetInactivityTimer() {
    clearTimeout(inactivityTimer);
    isIdle = false;
    inactivityTimer = setTimeout(() => {
        isIdle = true;
    }, 1000); // 1 second of inactivity before setting idle state
}

function animate() {
    updateWaves();
    applyWaveEffects();
    drawDots();
    requestAnimationFrame(animate);
}

function startWaves() {
    waveInterval = setInterval(createRandomWave, 250);
}

// Function to stop the wave creation
function stopWaves() {
    clearInterval(waveInterval);
}

function handleVisibilityChange() {
    if (document.hidden) {
        stopWaves(); // Stop the waves when the page is not visible
    } else {
        startWaves(); // Resume the waves when the page is visible
    }
}

window.addEventListener("resize", resizeCanvas);
window.addEventListener("mousemove", handleMouseMove);
document.addEventListener('visibilitychange', handleVisibilityChange);
resizeCanvas();
animate();

startWaves();




