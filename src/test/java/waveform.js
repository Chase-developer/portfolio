const canvas = document.getElementById('waveCanvas');
const ctx = canvas.getContext('2d');

let dots = [];
let waves = []; // Store active waves
const dotSpacing = 30;
const maxWaveRadius = 800; // Maximum travel distance before dying out
const waveSpeed = 3; // How fast the wave expands
const waveFadeSpeed = 0.01; // How quickly the wave loses intensity
const baseOpacity = 0.1; // Default minimum opacity for dots
const minDotSize = 2; // Default dot size
const maxDotSize = 10; // Maximum dot size when affected
const growSpeed = 0.05; // How quickly dots change size
let inactivityTimer;
let isIdle = false; // Track inactivity state

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
                targetSize: minDotSize // Start at min size
            });
        }
    }
}

function drawDots() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.fillStyle = "white";

    dots.forEach(dot => {
        ctx.beginPath();
        ctx.globalAlpha = dot.opacity;
        ctx.arc(dot.x, dot.y, dot.size, 0, Math.PI * 2);
        ctx.fill();

        // Gradually fade dots back to base opacity
        dot.opacity = Math.max(baseOpacity, dot.opacity - waveFadeSpeed);

        // Smoothly transition dot size
        dot.size += (dot.targetSize - dot.size) * growSpeed;
    });

    ctx.globalAlpha = 1; // Reset transparency
}

function updateWaves() {
    waves.forEach(wave => {
        wave.radius += waveSpeed; // Expand the wave
        wave.opacity -= waveFadeSpeed * 2; // Decrease wave intensity
    });

    // Remove waves that have completely faded
    waves = waves.filter(wave => wave.opacity > 0);
}

function handleMouseMove(event) {
	isIdle = false;
	resetInactivityTimer(); // Reset idle timer
    // Create a new expanding wave
	const { clientX, clientY } = event;
    waves.push({ x: clientX, y: clientY, radius: 0, opacity: 1.0 });
}

function applyWaveEffects() {
    dots.forEach(dot => {
        let maxEffect = 0; // Track the strongest effect from all waves

        waves.forEach(wave => {
            const dx = dot.x - wave.x;
            const dy = dot.y - wave.y;
            const distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < wave.radius) {
                let effectStrength = wave.opacity * (1 - distance / wave.radius); // Stronger near wave center
                maxEffect = Math.max(maxEffect, effectStrength);
            }
        });

        // Apply max effect to opacity and size
        dot.opacity = Math.min(1, baseOpacity + maxEffect);
        dot.targetSize = minDotSize + maxEffect * (maxDotSize - minDotSize);
    });
}

function createRandomWave() {
    if (isIdle) {
        const randomX = Math.random() * canvas.width;
        const randomY = Math.random() * canvas.height;
        waves.push({ x: randomX, y: randomY, radius: 0, opacity: 1.0 });
    }
}

function resetInactivityTimer() {
    clearTimeout(inactivityTimer);
    isIdle = false;
    inactivityTimer = setTimeout(() => {
        isIdle = true;
    }, 1000); // 2 seconds of inactivity before setting idle state
}

function animate() {
    updateWaves();
    applyWaveEffects();
    drawDots();
    requestAnimationFrame(animate);
}

window.addEventListener("resize", resizeCanvas);
window.addEventListener("mousemove", handleMouseMove);
resizeCanvas();
animate();

setInterval(createRandomWave, 250);


