const header = document.querySelector("#site-header");
const menuButton = document.querySelector("#menu-toggle");
const navigation = document.querySelector("#primary-nav");
const navLinks = [...document.querySelectorAll(".nav-link")];
const sections = [...document.querySelectorAll("main section[id]")];
const apiStatusElements = [...document.querySelectorAll("[data-api-status]")];

function setMenu(open) {
  navigation.classList.toggle("open", open);
  menuButton.setAttribute("aria-expanded", String(open));
  menuButton.setAttribute("aria-label", open ? "Close navigation" : "Open navigation");
  document.body.classList.toggle("menu-open", open);
}

menuButton.addEventListener("click", () => {
  setMenu(menuButton.getAttribute("aria-expanded") !== "true");
});

navigation.addEventListener("click", (event) => {
  if (event.target.closest("a")) {
    setMenu(false);
  }
});

document.addEventListener("keydown", (event) => {
  if (event.key === "Escape") {
    setMenu(false);
  }
});

function updateNavigation() {
  header.classList.toggle("scrolled", window.scrollY > 24);

  const currentSection = [...sections]
    .reverse()
    .find((section) => window.scrollY + 150 >= section.offsetTop);

  navLinks.forEach((link) => {
    link.classList.toggle(
      "active",
      currentSection && link.getAttribute("href") === `#${currentSection.id}`,
    );
  });
}

window.addEventListener("scroll", updateNavigation, { passive: true });
updateNavigation();

const revealObserver = new IntersectionObserver(
  (entries, observer) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add("visible");
        observer.unobserve(entry.target);
      }
    });
  },
  { threshold: 0.13 },
);

document.querySelectorAll(".reveal").forEach((element) => revealObserver.observe(element));
document.querySelector("#current-year").textContent = new Date().getFullYear();

function setApiStatus(status) {
  const labels = {
    checking: "Connecting",
    online: "API online",
    offline: "API offline",
  };

  apiStatusElements.forEach((element) => {
    element.classList.remove("checking", "online", "offline");
    element.classList.add(status);
    element.lastChild.textContent = ` ${labels[status]}`;
  });
}

function createElement(tag, className, text) {
  const element = document.createElement(tag);
  if (className) element.className = className;
  if (text !== undefined) element.textContent = text;
  return element;
}

function renderStats(stats) {
  const grid = document.querySelector("#stats-grid");
  const cards = stats.map((stat) => {
    const card = createElement("article", "stat-card");
    card.append(createElement("strong", "", stat.value));
    card.append(createElement("span", "", stat.label));
    return card;
  });
  grid.replaceChildren(...cards);
}

function renderSkills(skills) {
  const grid = document.querySelector("#skills-grid");
  const cards = skills.map((skill, index) => {
    const card = createElement("article", `skill-card${index === 1 ? " featured" : ""}`);
    card.append(createElement("p", "card-number", String(index + 1).padStart(2, "0")));
    card.append(createElement("h3", "", skill.name));

    const list = createElement("div", "skill-list");
    skill.items.forEach((item) => list.append(createElement("span", "", item)));
    card.append(list);
    return card;
  });
  grid.replaceChildren(...cards);
}

function renderProjects(projects) {
  const grid = document.querySelector("#projects-grid");
  const cards = projects.map((project, index) => {
    const card = createElement("article", `project-card${index % 2 === 1 ? " accent" : ""}`);

    const topLine = createElement("div", "project-topline");
    topLine.append(createElement("span", "project-index", String(index + 1).padStart(2, "0")));
    topLine.append(createElement("span", "project-status", project.status));

    const content = document.createElement("div");
    content.append(createElement("h3", "", project.name));
    content.append(createElement("p", "", project.description));

    const tags = createElement("div", "tag-list");
    project.technologies.forEach((technology) => tags.append(createElement("span", "", technology)));

    card.append(topLine, content, tags);
    return card;
  });

  grid.replaceChildren(...cards);
  grid.querySelectorAll(".project-card").forEach((card) => {
    card.classList.add("visible");
  });
}

async function loadPortfolio() {
  try {
    const response = await fetch("/api/portfolio", {
      headers: { Accept: "application/json" },
    });

    if (!response.ok) throw new Error(`API returned ${response.status}`);

    const portfolio = await response.json();
    renderStats(portfolio.stats);
    renderSkills(portfolio.skills);
    renderProjects(portfolio.projects);
    setApiStatus("online");
  } catch (error) {
    console.warn("Portfolio API is unavailable; showing built-in content.", error);
    setApiStatus("offline");
  }
}

loadPortfolio();

const form = document.querySelector("#contact-form");
const formStatus = document.querySelector("#form-status");
const messageInput = document.querySelector("#message");
const messageCount = document.querySelector("#message-count");
const submitButton = form.querySelector("button[type='submit']");

messageInput.addEventListener("input", () => {
  messageCount.textContent = messageInput.value.length;
});

function clearErrors() {
  form.querySelectorAll(".field-error").forEach((element) => {
    element.textContent = "";
  });
  form.querySelectorAll(".invalid").forEach((element) => {
    element.classList.remove("invalid");
    element.removeAttribute("aria-invalid");
  });
  formStatus.textContent = "";
  formStatus.className = "form-status";
}

function showFieldError(field, message) {
  const input = form.elements[field];
  const error = form.querySelector(`[data-error-for="${field}"]`);
  if (!input || !error) return;

  input.classList.add("invalid");
  input.setAttribute("aria-invalid", "true");
  error.textContent = message;
}

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  clearErrors();

  if (!form.checkValidity()) {
    form.reportValidity();
    return;
  }

  const payload = Object.fromEntries(new FormData(form).entries());
  submitButton.disabled = true;
  form.setAttribute("aria-busy", "true");
  submitButton.querySelector("span").textContent = "Sending...";
  formStatus.textContent = "Saving your message securely...";

  try {
    const response = await fetch("/api/messages", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
      },
      body: JSON.stringify(payload),
    });

    const result = await response.json().catch(() => ({}));

    if (!response.ok) {
      if (result.errors) {
        Object.entries(result.errors).forEach(([field, message]) => showFieldError(field, message));
      }
      throw new Error(result.message || "Your message could not be sent.");
    }

    form.reset();
    messageCount.textContent = "0";
    formStatus.textContent = result.message || "Thanks! Your message has been saved.";
    formStatus.className = "form-status success";
  } catch (error) {
    const serverError = error.message === "Failed to fetch";
    formStatus.textContent = serverError
      ? "The server is not running. Start Spring Boot and try again."
      : error.message;
    formStatus.className = "form-status error";
  } finally {
    submitButton.disabled = false;
    form.removeAttribute("aria-busy");
    submitButton.querySelector("span").textContent = "Send message";
  }
});
